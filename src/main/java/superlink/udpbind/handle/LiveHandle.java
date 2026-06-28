package superlink.udpbind.handle;

import com.alibaba.fastjson2.JSON;
import superlink.init.InitClass;
import superlink.init.UserLinkCon;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteReBuffer;
import superlink.udpbind.client.recives.ByteRsBuffer;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.recives.recor.Bindrec;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.controller.UdpBindSend;
import superlink.udpbind.servlet.ClearUser;
import superlink.udpbind.servlet.LiveReturnServer;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Tool;
import superlink.util.Utils;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.filemanage.xmltool.UserGet.UserSynServer;

public class LiveHandle implements Runnable{

    public LiveHandle(boolean mode){
        this.mode=mode;
    }

    public static List<String> list=new ArrayList<>();
    public static List<String> listbuf=new ArrayList();
    public static boolean resendOver=false;
    public List<String> strings1=new LinkedList<>();
    public List<String> strings2=new LinkedList<>();
    public List<String> strings3=new LinkedList<>();
    public boolean mode=true;
    public long time=1300;
    public long operaTime=System.currentTimeMillis();
    InetAddress localhost=null;

    @Override
    public void run() {
        int sl=0;
        Thread.currentThread().setName("liveHandle");
        long tu=0;
        while (mode){
            try {
            tu=System.currentTimeMillis();

            try {
                for (Map.Entry reCallBind:ReCallBind.traversalMap.entrySet()){
                    ((ReCallBind)reCallBind.getValue()).call();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }



            appreed(listbuf);
          //  long timestar=System.currentTimeMillis();
            synchronized (this){
                Iterator i=list.iterator();
                while (i.hasNext()){
                    String name= (String) i.next();
                    UserContext userContext= null;
                    try {
                        i.remove();
                        userContext = UDPclient.mainDataQueue.getUserContext(name);
                        Integer userid=userContext.getBothId();
                        byte[] bytes=("LL"+ UDPclient.userlocal.username).getBytes();
                        bytes= Utils.byteMerger(Utils.getUseridByte(userid,(short)0),bytes);
                        DatagramPacket d=new DatagramPacket(bytes,bytes.length,userContext.inetAddress,userContext.port);
                        //UDPclient.socket.send(d);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            if (sl%2==0){
                Iterator i= MainDataQueue.quemap.values().iterator();
                while (i.hasNext()){
                    try {
                        UserContext userContext = (UserContext) i.next();
                        Integer userid=userContext.getBothId();
                        byte[] bytes=("LL"+ UDPclient.userlocal.username).getBytes();
                        bytes= Utils.byteMerger(Utils.getUseridByte(userid,(short)0),bytes);
                        DatagramPacket d=new DatagramPacket(bytes,bytes.length,userContext.inetAddress,userContext.port);
                        Senders.Sends0(d);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }

            if (strings1.size()>0||strings2.size()>0||strings3.size()>0){
                for (int i = 0, len=strings1.size(); i <len ; i++) {
                    String s=strings1.get(i);
                    User user = UDPclient.userMap.get(s);
                    UserRequest userRequest= Tool.UsertoUserRequestbind(user);
                    UdpBindSend.udpBindSend(userRequest);
                }
                strings1.clear();
                for (int i = 0, len=strings2.size(); i <len ; i++) {
                    String s=strings2.get(i);
                    User user = UDPclient.userMap.get(s);
                    UdpBindSend.UdpBindSendinlocal(user);
                }
                strings2.clear();
                for (int i = 0, len=strings3.size(); i <len ; i++) {
                    String s=strings3.get(i);
                    User user = UDPclient.userMap.get(s);
                    UdpBindSend.udpReturnBind(user);
                }
                strings3.clear();
            }
            if (sl%16==0){
                DatagramPacket packet = new DatagramPacket( new byte[0],0,UDPclient.serverip,UDPclient.serverport);
                Iterator i= BindFactory.bindrecs.values().iterator();
                while (i.hasNext()){
                    try {
                        Bindrec bindrec = (Bindrec) i.next();
                        if (bindrec.deals.i==0){
//                                UdpBindSend.udpReturnBind(UDPclient.userMap.get(bindrec.username));
                            UserContext userContext=UDPclient.mainDataQueue.getUserContext(bindrec.username);
                            if (userContext.sort==1){
                                strings1.add(bindrec.username);
                            }
                            if (userContext.sort==2){
                                strings2.add(bindrec.username);
                            }
                            if (userContext.sort==3){
                                strings3.add(bindrec.username);
                            }
                        }
                        packet.setData(("QU"+bindrec.username).getBytes());
                        Senders.Sends0(packet);
                        bindrec.deals.i=0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }

            InetAddress localhost=null;
            try {
                if (InitClass.ipv){
                    localhost=Utils.getLocalIpv4();
                }else {
                    localhost=Utils.getLocalIpv6();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((this.localhost==null && localhost!=null) ||UDPclient.mainDataQueue.reciverques.thread==null ){
                UDPclient.mainDataQueue.startMainQue(true);
            }

            this.localhost = localhost;

            //重lian
            if ((!UDPclient.userlocal.inaddress.equals(localhost)) && localhost!=null){
//                UDPclient.userlocal.inaddress=localhost;
//                try {
//                    UDPclient.socket=new DatagramSocket(UDPclient.userlocal.inport);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                User user= Tool.copyUser(UDPclient.userlocal);
                user.inaddress=localhost;
//                JSON.toJSONString(user)
                String send= user.toString();
                byte[] data = send.getBytes();//将接收到的数据变成字节数组
                DatagramPacket packet = new DatagramPacket(data, data.length, UDPclient.serverip, UDPclient.serverport);
                Senders.Sends0(packet);
            }
            if (!resendOver) {
                rs();
            }
//                DatagramSocket buf=socket;
//                try {
//                    socket=new DatagramSocket(userlocal.inport);
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }

            sl++;
            if ((sl%30)==0){
                sl();
            }
//            else{
//                sl0();
//            }

            if(sl>60){
                if (!UDPclient.userlocal.inaddress.equals(UDPclient.userlocal.address)){
                    resendOver=false;
                }
                rs();
                sl=0;
            }
            operaTime=System.currentTimeMillis();
            tu=operaTime-tu;
            Thread.currentThread().isInterrupted();
                Thread.sleep(time-tu);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    int sl=0;
    long tu=0;
    public void run(long time) {
        try {
            tu=System.currentTimeMillis();
            if (ReCallBind.traversalMap.size()!=0){
                try {
                    for (Map.Entry reCallBind:ReCallBind.traversalMap.entrySet()){
                        ((ReCallBind)reCallBind.getValue()).call();
                        if ((tu-System.currentTimeMillis())>time){
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }


            if(UDPclient.mainDataQueue.reciverques==null ||
                    UDPclient.mainDataQueue.reciverques.thread==null||
                    UDPclient.mainDataQueue.reciverques.thread.getState()!= Thread.State.RUNNABLE
            ){
                UDPclient.mainDataQueue.reSet();
                UDPclient.mainDataQueue.startMainQue(true);
            }

            appreed(listbuf);

            synchronized (this){
                Iterator i=list.iterator();
                while (i.hasNext()){
                    String name= (String) i.next();
                    UserContext userContext= null;
                    try {
                        i.remove();
                        userContext = UDPclient.mainDataQueue.getUserContext(name);
                        Integer userid=userContext.getBothId();
                        byte[] bytes=("LL"+ UDPclient.userlocal.username).getBytes();
//                        bytes= Utils.byteMerger(Utils.getUseridByte(userid,(short)0),bytes);
////                        System.out.println("live "+name);
//                        DatagramPacket d=new DatagramPacket(bytes,bytes.length,userContext.inetAddress,userContext.port);
//                        Senders.Sends0(d);
//
//                        ByteRsBuffer byteRsBuffer= (ByteRsBuffer) userContext.getQueue(5);
//                        byteRsBuffer.add(bytes);

                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }

//            if (sl%2==0){
                Iterator<Map.Entry<Integer, UserContext>> in= MainDataQueue.quemap.entrySet().iterator();
                while (in.hasNext()){
                    try {
                        Map.Entry<Integer, UserContext> entry= in.next();
                        if (entry.getKey()==0 || UDPclient.userlocal.username.equals(entry.getValue().userName)){
                            continue;
                        }
                        UserContext userContext = entry.getValue();
//                        UserContext userContext = (UserContext) in.next();
                        int userid=userContext.getBothId();
                        userContext.waitTime=System.currentTimeMillis();
                        byte[] bytes="LL".getBytes();
                        bytes= Utils.byteMerger(Utils.getUseridByte(userid,(short)0),bytes);

                        if (userContext.sort==3 ){
                            Senders.Sends( LiveReturnServer.inetAddress,LiveReturnServer.port,
                                    Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{127,127}));
                        }
                        Senders.Sends(userContext.inetAddress,userContext.port,bytes);

                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                if(sl%12==0){
                    Iterator<Bindrec> ib= BindFactory.bindrecs.values().iterator();
                    while (ib.hasNext()){
                        try {
                            Bindrec bindrec = ib.next();
                            UserContext userContext=UDPclient.mainDataQueue.getUserContext(bindrec.username);
                            ByteRsBuffer byteRsBuffer= (ByteRsBuffer) userContext.getQueue((short)5);
                            for (int j = 0; j < 7; j++) {
                                byteRsBuffer.add((String.valueOf(j)+String.valueOf(j)).getBytes());
                            }
                        }catch (Exception e){
                        }
                    }
                }

//            }

            if (strings1.size()>0||strings2.size()>0||strings3.size()>0){
                for (int i = 0, len=strings1.size(); i <len ; i++) {
                    String s=strings1.get(i);
                    User user = UDPclient.userMap.get(s);
                    if (user==null){
                        continue;
                    }
                    Bindrec bindrec=BindFactory.bindrecs.get(s);
                    if (bindrec!=null){
                        if (bindrec.deals.i>0){
                            continue;
                        }
                    }
                    UserRequest userRequest= Tool.UsertoUserRequestbind(user);
                    UdpBindSend.udpBindSend(userRequest);
                }
                strings1.clear();
                for (int i = 0, len=strings2.size(); i <len ; i++) {
                    String s=strings2.get(i);
                    User user = UDPclient.userMap.get(s);
                    if (user==null){
                        continue;
                    }
                    Bindrec bindrec=BindFactory.bindrecs.get(s);
                    if (bindrec!=null){
                        if (bindrec.deals.i>0){
                            continue;
                        }
                    }
                    UdpBindSend.UdpBindSendinlocal(user);
                }
                strings2.clear();
                for (int i = 0, len=strings3.size(); i <len ; i++) {
                    String s=strings3.get(i);
                    User user = UDPclient.userMap.get(s);
                    if (user==null){
                        continue;
                    }
                    Bindrec bindrec=BindFactory.bindrecs.get(s);
                    if (bindrec!=null){
                        if (bindrec.deals.i>0){
                            continue;
                        }
                    }
                    UdpBindSend.udpReturnBind(user);
                }
                strings3.clear();
            }

            if (sl%12==0 && sl!=0){
                DatagramPacket packet = new DatagramPacket( new byte[0],0,UDPclient.getServerip(),UDPclient.getSport());
                Iterator<Bindrec> i= BindFactory.bindrecs.values().iterator();
                byte[] bytes;
                while (i.hasNext()){
                    try {
                        Bindrec bindrec = i.next();
                        if (bindrec.deals.i==0){
//                                UdpBindSend.udpReturnBind(UDPclient.userMap.get(bindrec.username));
                            UserContext userContext=UDPclient.mainDataQueue.getUserContext(bindrec.username);
                            if (userContext.sort==1){
                                strings1.add(bindrec.username);
                            }
                            if (userContext.sort==2){
                                strings2.add(bindrec.username);
                            }
                            if (userContext.sort==3){
                                strings3.add(bindrec.username);
                            }
                            bytes=Utils.byteMerger(new byte[6],("QU"+bindrec.username).getBytes());
                            packet.setData(bytes);
                            Senders.Sends0(packet);
                            bytes=Utils.byteMerger(("QU"+bindrec.username).getBytes());
                            packet.setData(bytes);
                            Senders.Sends0(packet);
                        }else {
                            add(bindrec.username);
                        }

                        bindrec.deals.i=0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }


            if(UserLinkCon.linkConList.size()>0){
                for (UserLinkCon userLinkCon:UserLinkCon.linkConList){
                    UserContext userContext = UDPclient.getUser(userLinkCon.user);
                    if (userContext==null){
                        User user =UDPclient.userMap.get(userLinkCon.user);
                        userLinkCon.link(user);
                    }else {
                        userLinkCon.inital();
                    }
                }
            }


//            UDPclient.initSocket();
//            try {
//                UDPclient.socket.send(datagramPacket);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
            DatagramPacket datagramPacket=new DatagramPacket(new byte[]{76,76},2,UDPclient.getServerip(),UDPclient.getSport());
            try {
                UDPclient.socket.send(datagramPacket);
            } catch (IOException e) {
                if (e instanceof java.net.SocketException) {
                    // 2. 获取异常消息（注意：消息内容可能因操作系统或JDK版本略有不同，建议转为小写比较）
                    String msg = e.getMessage();

                    // 3. 判断消息是否包含 "Socket is closed"
                    // 注意：有时消息可能为 null，或者包含 "Socket closed" (没有 is)，建议做模糊匹配
                    if (msg != null && msg.toLowerCase().contains("socket is closed")) {
                        System.out.println("【捕获确认】这是 Socket is closed 异常，说明本地已主动关闭连接，但后续又进行了读写。");
                        UDPclient.initSocket();
                        // 这里处理你的特定逻辑，比如忽略该异常或记录日志
                    } else {
                        // 处理其他 SocketException，如 Connection reset, Broken pipe 等
                        System.out.println("【其他异常】Socket 异常但非关闭状态: " + msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                UDPclient.initSocket();
                try {
                    UDPclient.socket.send(datagramPacket);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            if (MainDataQueue.getMainReciverques(UDPclient.socket).thread==null && InitClass.ThreadMode!=1){
                System.out.println("MainReacter   rSstart");
                UDPclient.mainDataQueue.startMainQue(true);
            }
            InetAddress localhost=null;
            try {
                if (InitClass.ipv){
                    localhost=Utils.getLocalIpv4();
                }else {
                    localhost=Utils.getLocalIpv6();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.localhost = localhost;

            //重lian
            if ((localhost!=null &&(!localhost.equals(UDPclient.userlocal.inaddress)))
                    || !UserSynServer){
                User user= Tool.copyUser(UDPclient.userlocal);
                user.inaddress=localhost;
                String send= user.toString();
                byte[] data = send.getBytes();//将接收到的数据变成字节数组
                DatagramPacket packet = new DatagramPacket(data, data.length, UDPclient.serverip, UDPclient.serverport);
                Senders.Sends0(packet);
                Senders.Sends0(packet);
                UDPclient.userlocal.inaddress = localhost;
                UserSynServer=false;
            }
            if (!resendOver) {
                rs();
            }


            if ((sl%30)==0){
                sl();
            }

            if((sl%60)==0){//3min
                if (!UDPclient.userlocal.inaddress.equals(UDPclient.userlocal.address)){
                    resendOver=false;
                }
                rs();
                sl=0;
                ClearUser.chaek();
            }
            if((sl%180)==0){
                if (UDPclient.userlocal.time==0){
                    UDPclient.initSocket();
                }else {
                    UDPclient.userlocal.time=0;
                }
                sl=0;
            }
            sl++;
            operaTime=System.currentTimeMillis();
//            time=System.currentTimeMillis()-tu;
//            System.out.println("all time:"+time);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    ReentrantLock inputlock=new ReentrantLock();
    public void add(String s){
        synchronized (inputlock){
            listbuf.add(s);
        }
    }
    public void appreed(List l){
        synchronized (inputlock){
            Iterator i=listbuf.iterator();
            while (i.hasNext()){
                list.add((String) i.next());
                i.remove();
            }
        }
    }

    public void rs(){
        String send = UDPclient.userlocal.toString();
        byte[] data = send.getBytes();//将接收到的数据变成字节数组
        DatagramPacket packet = new DatagramPacket(data, data.length, UDPclient.getServerip(), UDPclient.getSport());
        Senders.Sends0(packet);
    }

    public void sl(){
        String send="LL";
        byte[] data = send.getBytes();//将接收到的数据变成字节数组
        DatagramPacket packet = new DatagramPacket(data, data.length, UDPclient.getServerip(), UDPclient.getSport());
        Senders.Sends0(packet);
    }
    public void sl0(){
        String send="L";
        byte[] data = send.getBytes();//将接收到的数据变成字节数组
        DatagramPacket packet = new DatagramPacket(data, data.length, UDPclient.serverip, UDPclient.serverport);
        Senders.Sends0(packet);
    }
//    public void removeObjectList(String username){
//        Map map= ActionIndex.objectList.get(username);
//        if (map!=null){
//            if (map.size()!=0){
//                map.forEach((k,v)->{
//                    if (v instanceof Runnable){
//                        try {
//                            ((Runnable)v).run();
//                        }catch (Exception |Error e){
//                            e.printStackTrace();
//                        }
//                        map.remove(k);
//                    }
//                });
//            }
//        }
//    }

}
