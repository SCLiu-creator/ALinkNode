package superlink.udpbind.client;

import com.alibaba.fastjson2.annotation.JSONField;
import superlink.udpbind.client.recives.*;
import superlink.udpbind.remote.block.RemoteBlock;
import superlink.udpbind.servlet.LiveReturnServer;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class UserContext{
    public String userName;
    private idbi idbi;
    public InetAddress inetAddress;
    public int port;
    @JSONField(serialize = false)
    public  Map<Short, ByteBufer> map;
    //1 3,returnbind
    public int sort;
    public long waitTime=System.currentTimeMillis();
    public long delayTime = 3000;

    @JSONField(serialize = false)
    public  Map<Short, Task> taskMap;

    public UserContext(String userName,InetAddress inetAddress,int port){
        this.userName = userName;
        this.inetAddress= inetAddress;
        this.port= port;
        map =new ConcurrentHashMap<>();
        taskMap=new ConcurrentHashMap<>();
        map.put((short)0,new ByteQueue(20));
        map.put((short)5,new ByteRsBuffer(8).setUser(this));
        map.put((short)7,new ByteReBuffer(8).setUser(this));
        makeii();
    }

    public UserContext setUserContext(User user){
        switch (sort){
            case 1:{
                inetAddress=user.address;
                port=user.port;
                break;
            }
            case 2:{
                inetAddress=user.inaddress;
                port=user.inport;
                break;
            }
            case 3:{
                inetAddress= LiveReturnServer.inetAddress;
                port=LiveReturnServer.port;
                break;
            }
        }
        return this;
    }

    public void succeedSort(){
        if (sort<0){
            sort= -sort;
        }
        MainDataQueue.callBack(this);
    }

    public void overUserContext(){
        try {
            UDPclient.mainDataQueue.delUser(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.inetAddress=null;
        map=null;
    }

    public ByteBufer getDataQue(short id){
        if (map.containsKey(id)){
            return map.get(id);
        }else {
            ByteBufer blockingQueue=new ByteQueue(256);
            map.put(id,blockingQueue);
            return blockingQueue;
        }
    }

    public ByteBufer getQueue(short id){
        return map.get(id);
    }
    public void setQueue(Short id,ByteBufer blockingQueue){
        map.put( id,blockingQueue);
    }

    public synchronized Object deltask(Short id){
        map.remove(id);
        return taskMap.remove(id);
    }

    /*
    * 创建新接收队列，返回队列id
    * */
    public short newQueue(ByteBufer blockingQueue){
        Random random=new Random();
        short id;
        while (true){
             id = (short) random.nextInt(99);
            if (map.containsKey(id)){
                continue;
            }else {break;}
        }
        map.put((short)id,blockingQueue);
        return id;

    }
    public short newQueue(){
        ByteBufer blockingQueue=new ByteQueLink();
        Random random=new Random();
        short id;
        while (true){
            id = (short) (random.nextInt(254)+1);
            if (map.containsKey(id)){
                continue;
            }else {break;}
        }
        map.put((short)id,blockingQueue);

        System.out.println("newQueue:"  +id + " \n" + "user: "+this.userName+"  userid: "+this.getBothId());
        return id;
    }
    public short newQueue(int l){
        ByteBufer blockingQueue=new ByteQueue(l);
        Random random=new Random();
        short id;
        while (true){
            id = (short) (random.nextInt(254)+1);
            if (map.containsKey(id)){
                continue;
            }else {break;}
        }
        map.put((short)id,blockingQueue);
        System.out.println("newQueue:"  +id + " \n" + "user: "+this.userName+"  userid: "+this.getBothId());
        return id;
    }

    public void stableSend(byte[] bytes){
        ByteRsBuffer byteRsBuffer= (ByteRsBuffer) getQueue((short) 5);
        byteRsBuffer.add(bytes);
//        Senders.Sends(getBothId(),5,inetAddress,port,bytes);
    }


    public Task newTask(){
        short id= newQueue();
        Task task=new Task(id);
        taskMap.put(id,task);
        System.out.println("newTask:"  +id + " \n" + "user: "+this.userName+"  userid: "+this.getBothId());
        return task;
    }
    public Task newTask(short id){
        Task task=new Task(id);
        taskMap.put(id,task);
        System.out.println("newTask:"  +id + " \n" + "user: "+this.userName+"  userid: "+this.getBothId());
        return task;
    }
    public Task getTask(short id){
        Task task=taskMap.get(id);
        if (task==null){
            task=newTask(id);
        }
        return task;
    }

    //本地生成
    public UserContext setUserId(int id){
        idbi.setUserId(id);
        return this;
    }
    //对方生成
    public UserContext setBothId(int di){
        idbi.setBothId(di);
        return this;
    }
    public int getUserId(){
        return idbi.getUserId();
    }
    public int getBothId(){
        return idbi.getBothId();
    }
    public void makeii(){

        idbi i=new idbi();
        this.idbi=i;
    }
    public boolean cheak(){
        return UDPclient.mainDataQueue.getUserContext(getUserId())==null;
    }
    public long getTime(){
        return delayTime;
    }
    class idbi{
        //本机存储的对方id
        int userId;
        //对方存储的本机id
        int bothId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getBothId() {
            return bothId;
        }

        public void setBothId(int bothId) {
            this.bothId = bothId;
        }

        @Override
        public String toString() {
            return "idbi{" +
                    "userId=" + userId +
                    ", bothId=" + bothId +
                    '}';
        }
    }

//    @Override
//    public void finalize() throws Exception {
//        System.out.println("UserConText finalize;"+ "  "+user.username);
//    }

    public void send(int id,byte[] data){
        Senders.Sends(getBothId(),id,inetAddress,port,data);
    }

    public void over(){
        System.out.println("UserConText.over(user.username);"+ "  "+ userName);
        taskMap.forEach((k,v)->{
            v.block.mode=0;
            v.block.change=false;
        });
        taskMap.clear();
        map.clear();
    }

    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode()==obj.hashCode()&&this.getClass().equals(obj.getClass());
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "userName='" + userName + '\'' +
                ", idbi=" + idbi +
                ", inetAddress=" + inetAddress +
                ", port=" + port +
                ", sort=" + sort +
                '}';
    }

    public boolean isRun(short sort){
        Task task=taskMap.get(sort);
        return task.count>0;
    }
    public int getState(short sort){
        Task task=taskMap.get(sort);
        return task.count;
    }

    public class Task{
        public Task(short id){
            block=new RemoteBlock(UserContext.this,id,this);
        }
        public int count=0;
        public Object task;
        public RemoteBlock block;

        public void inLock(){
            block.lock();
        }
        public void unLock(){
            block.unLock();
            block.runs();
        }
    }

    public static void main(String[] args) {
        String s= "{\"username\":\"YmRcLeWBr9EOTzsu\",\"requestaddress\":\"49.93.187.34\",\"requestport\":9137,\"toaddress\":\"49.93.187.34\",\"toport\":9020,\"inaddress\":\"192.168.72.147\",\"userid\":632420454,\"inport\":23016,\"request\":false,\"choose\":0,}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ";
        String s1="{\"username\":\"YmRcLeWBr9EOTzsu\",\"requestaddress\":\"49.93.187.34\",\"requestport\":9137,\"toaddress\":\"49.93.187.34\",\"toport\":9020,\"inaddress\":\"192.168.72.147\",\"userid\":615432573,\"inport\":23016,\"request\":false,\"choose\":0,}";
    }
}
