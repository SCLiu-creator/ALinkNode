package superlink.udpbind.client.server;

import superlink.httpserver.servelt.ProcessMapL;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.JackJson;
import superlink.util.Utils;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerCon {

    ByteBufer byteBufer;
    ByteBufer readBufer;
    ArrayList<byte[]> arrayList;

    public static ConcurrentHashMap<DataKey,UBS> readMap =new ConcurrentHashMap<>();
    public static ConcurrentHashMap<DataKey,USB> sendMap =new ConcurrentHashMap<>();
    public static ConcurrentHashMap<DataKey,USB> reSendMap =new ConcurrentHashMap<>();
    public static AtomicBoolean readBool=new AtomicBoolean(false);
    public static AtomicBoolean sendBool=new AtomicBoolean(false);
    public static ThreadPoolExecutor poolExecutor =
            new PriorityThreadPoolExecutor(0,2,10,1);
    public static ThreadPoolExecutor workExecutor =
            new PriorityThreadPoolExecutor(0,2,10,100);

    //待接收数据和回调任务
    public static class UBS {
        public UserContext userContext;
        public byte[] bytes;
        public ByteBuferPacket buferPacket;
        public boolean run=true;
        public Runnable runnable;
        public boolean start=false;
        public short id;
        public UBS(UserContext userContext,short id,int size){
            this.id=id;
            this.buferPacket=new ByteBuferPacket(size);
            userContext.setQueue(id,buferPacket);
            bytes=Utils.byteMerger(Utils.intToByteArray(userContext.getBothId()),
                    Utils.shortToByteArray(id),new byte[]{'r','b'},new byte[4],new byte[2]);
            this.userContext=userContext;
        }
        public void restLen(int size){
            if(this.buferPacket.items.length!=0){
//                ByteBuferPacket packet=new ByteBuferPacket(size);
            }else {
                this.buferPacket=new ByteBuferPacket(size);
                userContext.setQueue(id,buferPacket);
            }
        }

        @Override
        public String toString() {
            return "UBS{" +
                    "userContext=" + userContext +
                    ", buferPacket=" + buferPacket +
                    ", run=" + run +
                    ", runnable=" + runnable +
                    ", start=" + start +
                    ", id=" + id +
                    '}';
        }
    }
    //待发送,请求发起方
    public static class USB {
        public UserContext userContext;
        public byte[] bytes;
        public boolean c=false;
        public short id;
        public DataPacket dataPacket;
        public USB(UserContext userContext,short id,byte[] data){
            this.id=id;
            this.userContext=userContext;
            bytes=Utils.byteMerger(Utils.intToByteArray(userContext.getBothId()),
                    Utils.shortToByteArray(id),new byte[4],new byte[2]);
            dataPacket=new DataPacket(data,userContext,id);
        }

        @Override
        public String toString() {
            return "USB{" +
                    "userContext=" + userContext +
                    ", id=" + id +
                    ", dataPacket=" + dataPacket +
                    '}';
        }
    }
    public static short getSerice(Object o,UserContext userContext){
        if (UDPclient.userlocal.username.equals(userContext.userName)){
            return 0;
        }
        short id=userContext.newQueue();
        String s= JackJson.toJson(o);
        USB usb=new USB(userContext,id,s.getBytes());
        sendMap.put(new DataKey(userContext.getUserId(),id),usb);
        runsend();
        return id;
    }

    public static void getSerice(Object o,UserContext userContext,short id){
        if(o==null){o="";}
        String s= JackJson.toJson(o);
        USB usb=new USB(userContext,id,s.getBytes());
        sendMap.put(new DataKey(userContext.getUserId(),id),usb);
        runsend();
    }
    public static UBS setSerice(short id,int size,UserContext userContext){
        if (id==0){
            return null;
        }
        DataKey dataKey=new DataKey(userContext.getUserId(),id);
        UBS ubs= readMap.get(dataKey);
        if (ubs==null){
            ubs=new UBS(userContext,id,size);
            readMap.put(dataKey,ubs);
        }else {
            ubs.restLen(size);
        }
        runRead();
        return ubs;
    }
    public static UBS dealSerice(short id,UserContext userContext){
        DataKey dataKey=new DataKey(userContext.getUserId(),id);
        UBS ubs= readMap.get(dataKey);
        if (ubs==null){
            ubs=new UBS(userContext,id,0);
            ubs.runnable=defRun;
            readMap.put(dataKey,ubs);
        }
        runRead();
        return ubs;
    }
    static Runnable defRun=()->{
    };

    public static void runsend(){
        if (!sendBool.get()){
            if (threadSend!=null &&threadSend.getState()!=Thread.State.RUNNABLE){
                threadSend.interrupt();
            }else {
                poolExecutor.execute(runs);
            }

        }
    }
    public static void runRead(){
        if (!readBool.get()){
            if (threadRead!=null &&threadRead.getState()!=Thread.State.RUNNABLE){
                threadRead.interrupt();
            }else {
                poolExecutor.execute(runr);
            }
        }
    }

    public static Thread threadRead;
    static Runnable runr=()->{
        if (readBool.get()){
            return;
        }
        readBool.set(true);
        Thread.currentThread().setName("read");
        threadRead=Thread.currentThread();
        try {
            while (true){
                Iterator<Map.Entry<DataKey, UBS>> iterator= readMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<DataKey, UBS> entry=iterator.next();
                    UBS ubs=entry.getValue();
                    if (ubs.start==false){
                        continue;
                    }
                    UserContext userContext=ubs.userContext;
                    ByteBuferPacket bf=ubs.buferPacket;
                    if(bf==null){
                        readMap.remove(entry.getKey());
                        System.out.println("yichuufw");
                        continue;
                    }
                    boolean tb=true;
                    int j=64;
                    for (int i=bf.takeIndex;i<bf.items.length&& i<j+bf.takeIndex;i++){
                        if(bf.items[i]==null){
                            byte[] p= Utils.intToByteArray(i);
                            Senders.Sends(userContext.inetAddress,userContext.port,
                                    Utils.byteMerger(
                                            Utils.intToByteArray(userContext.getBothId()),
                                            Utils.shortToByteArray((short) 0),
                                            new byte[]{'r','b'},
                                            p,
                                            Utils.shortToByteArray(ubs.id)));
                            tb=false;
                        }else {
                            if (tb){
                                bf.takeIndex++;
                                j++;
                            }
                        }
                    }

                    if(bf.takeIndex==bf.items.length){
                        if (ubs.runnable==null){
                            ubs.runnable=()->{
                                Object jsre=null;
                                short id=Utils.byteArrayToshort(ubs.bytes,4);
                                try {
                                    byte[] bytes=ubs.buferPacket.getData();
                                    Map<String,Object> map=JackJson.toMap(new String(bytes));
                                    ubs.buferPacket=null;
                                    String uri= (String) map.get("url");
                                    String[] strings=uri.split("\\?",2);
                                    ProcessMapL.Nettybean nettybean=null;
                                    nettybean=ProcessMapL.map.get(strings[0]);
                                    if (nettybean==null){
                                        for (ProcessMapL.Nettybean n:ProcessMapL.list){
                                            if (strings[0].contains(n.murl)){
                                                nettybean=n;
                                                break;
                                            }
                                        }
                                    }
                                    jsre = nettybean.ire(map);
                                }finally {
                                    getSerice(jsre,userContext,id);
                                }
                            };
                        }
                        if (ubs.run){
                            workExecutor.execute(ubs.runnable);
                            ubs.run=false;
                        }else {
                            if (ubs.buferPacket!=null){
                                //请求删除资源
//                            System.arraycopy(ubs.bytes,6,new byte[]{'b','r'},0,2);
                                byte[] bytes=Utils.byteMerger(
                                        Utils.intToByteArray(userContext.getBothId()),
                                        Utils.shortToByteArray((short)0),
                                        "br".getBytes(),
                                        Utils.shortToByteArray(ubs.id));
                                Senders.Sends(userContext.inetAddress,userContext.port,bytes);
                            }else {
                                readMap.remove(entry.getKey());
                                System.out.println("yichuufw");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if (readMap.size()==0){
                    break;
                }
            }
        }finally {
            readBool.set(false);
        }

    };

    public static Thread threadSend;
    static Runnable runs=()->{
        if (sendBool.get()){
            return;
        }
        sendBool.set(true);
        threadSend=Thread.currentThread();
        threadSend.setName("send");
        try {
            while (true){

                Iterator<Map.Entry<DataKey, USB>> iterator= sendMap.entrySet().iterator();
                while (iterator.hasNext()){
                    USB ubs=  iterator.next().getValue();
                    UserContext userContext=ubs.userContext;
                    if (ubs.c){
                        DataPacket bf=ubs.dataPacket;
                        bf.send();
                    }else {
                        byte[] bytes=Utils.byteMerger(
                                Utils.intToByteArray(userContext.getBothId()),
                                Utils.shortToByteArray((short)0),
                                "bs".getBytes(),
                                Utils.shortToByteArray(ubs.id),
                                Utils.intToByteArray(ubs.dataPacket.getLen()));
                        Senders.Sends(userContext.inetAddress,userContext.port, bytes);
                    }
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if (sendMap.size()==0){
                    break;
                }
            }
        }finally {
            sendBool.set(false);
        }

    };

    Runnable runnable=()->{
        byte[] bytes;
        while (readBufer.size()>0){
            bytes=readBufer.poll();
            if (bytes!=null){
                if (bytes.length>0){
                    arrayList.add(bytes);
                }
            }
        }
    };

    Runnable srun=()->{
        byte[] bytes;
        while (readBufer.size()>0){
            bytes=readBufer.poll();
            if (bytes!=null){
                if (bytes.length>0){
                    arrayList.add(bytes);
                }
            }
        }
    };




    public static class DataKey{
        public int user;
        public short id;

        public DataKey(int user, short id) {
            this.user = user;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataKey dataKey = (DataKey) o;

            if (user != dataKey.user) return false;
            return id == dataKey.id;
        }

        @Override
        public int hashCode() {
            int result = user;
            result = 31 * result + (int) id;
            return result;
        }

        @Override
        public String toString() {
            return "DK{" +
                    "user=" + user +
                    ", id=" + id +
                    '}';
        }
    }
}
