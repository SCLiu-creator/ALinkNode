package superlink.udpbind.client.recives;

import org.checkerframework.checker.units.qual.A;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.recives.recor.Bindrec;
import superlink.udpbind.client.server.ByteBuferPacket;
import superlink.udpbind.usedata.User;
import superlink.util.prioityThreadPool.CustomRejectedExecutionHandler;
import superlink.util.prioityThreadPool.CustomThreadFactory;
import superlink.util.prioityThreadPool.LinkedReciveQueue;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class MainDataQueue {

    public static ThreadPoolExecutor mainthreadPoolExecutor =
            new PriorityThreadPoolExecutor(2,
                    20,
                    3,
                    TimeUnit.MINUTES,
                    new LinkedReciveQueue<>(200),
                    new CustomThreadFactory(),
                    new CustomRejectedExecutionHandler());
    //接收某端口，根据任务id存储
    public static Map<Integer, UserContext> quemap = new ConcurrentHashMap<Integer, UserContext>();
    public static Map<String, ArrayList<UserContext>> tranmap = new ConcurrentHashMap();
    public static Map<String, Integer> usermap = new ConcurrentHashMap<String, Integer>();
    public static Map<String, UserContext> ipportmap = new ConcurrentHashMap<String, UserContext>();
    public static Random random = new Random();

    public static Map<String, UserContext> createBuf = new ConcurrentHashMap<String, UserContext>();
    //根据名称获取任务id
//    public String requestname;//名称
    public User user;
    public Object lock = new Object();

    public static MainReciverques mainReciverques;
    public static MainReciverques getMainReciverques(DatagramSocket datagramSocket){
        if (mainReciverques==null){
           mainReciverques= new MainReciverques(datagramSocket);
        }else {
            if (!mainReciverques.datagramSocket.equals(datagramSocket)){
                mainReciverques.run=false;
                mainReciverques= new MainReciverques(datagramSocket);
            }
        }
        return mainReciverques;

    }

    public MainReciverques reciverques;
    public MainDataQueue() {
        new RuntimeException().printStackTrace();
        reciverques = getMainReciverques(UDPclient.socket);
        reciverques.setMode(true);
//        MainReciverques reciverques1 =new MainReciverques(socket,requestname,this);
//        mainthreadPoolExecutor.execute(reciverques1);
        synchronized (quemap) {
            UserContext userContext = createServerContext(UDPclient.serverip,UDPclient.serverport);
            quemap.put(0, userContext);
            usermap.put("server", 0);
            ipportmap.put(UDPclient.serverip+":"+UDPclient.serverport,userContext);
        }
    }
    public MainReciverques reSet(){
        reciverques = getMainReciverques(UDPclient.socket);
        reciverques.setMode(true);
        return reciverques;
    }
    public MainReciverques startMainQue(boolean b){
        if (b){
            mainthreadPoolExecutor.execute(reciverques);
            return null;
        }else {
            return reciverques;
        }
    }

    //根据名称获取数据
    public byte[] getdata(int username, short id, Long time) throws InterruptedException {
        UserContext context = quemap.get(username);
        ByteBufer blockingQueue = context.getQueue(id);
        byte[] bytes = blockingQueue.poll(time, TimeUnit.SECONDS);
        return bytes;
    }

    //根据id获取数据
//    public  byte[] getdata(int userid,Long time) throws InterruptedException {
//        BlockingQueue<byte[]> blockingQueue= quemap.get(userid);
//        byte[] bytes=blockingQueue.poll(time,TimeUnit.SECONDS);
//        return bytes;
//    }


    /* 输入目的端口和地址*/
    public UserContext createUserBuf(String name, InetAddress address, Integer port) {
        UserContext userContext = new UserContext(name,address,port);
        int userid;
        synchronized (quemap) {
            while (true) {
                userid = random.nextInt(Integer.MAX_VALUE);
                if (quemap.containsKey(userid)) {
                    continue;
                } else {
                    break;
                }
            }
            userContext.setUserId(userid);
            usermap.put(name, userid);
            createBuf.put(name,userContext);
        }
        return userContext;
    }

    public synchronized void removeUserBuf(String name) {
        Integer integer=MainDataQueue.usermap.get(name);
        if(integer==null||MainDataQueue.quemap.get(integer)==null){
            usermap.remove(name);
            BindFactory.bindrecs.remove(name);
        }
    }

    public UserContext addUserContext(UserContext userContext) {
        Integer integer = usermap.get(userContext.userName);
        if (integer==null) {
            return userContext;
        }
        quemap.put(integer, userContext);
        createBuf.remove(userContext.userName);
        return userContext;
    }

    /* 输入目的端口和地址*/
    public UserContext createUserContext(String name, InetAddress address, Integer port) {
        Integer integer = usermap.get(name);
        if (integer!=null &&quemap.get(integer)!=null) {
            UserContext userContext=quemap.get(integer);
            if ((!userContext.inetAddress.equals(address))||(userContext.port!=port)){
//                    quemap.remove(integer);
//                    break;
                userContext.inetAddress=address;
                userContext.port=port;
                return userContext;
            }else {
                return userContext;
            }
        }
        UserContext userContext = new UserContext(name,address,port);
        // String ip=address.toString()+":"+port;
        int userid;
        synchronized (quemap) {
            while (true) {
                userid = random.nextInt(Integer.MAX_VALUE);
                if (quemap.containsKey(userid)) {
                    continue;
                } else {
                    break;
                }
            }
            userContext.setUserId(userid);
            quemap.put(userid, userContext);
            usermap.put(name, userid);
        }
        return userContext;
    }

    /* 输入目的端口和地址*/
    public UserContext createtranUserContext(String name, InetAddress address, Integer port) {
        ArrayList<UserContext> userContexts = tranmap.get(user);
        if (userContexts!=null && userContexts.size()>0){
            return userContexts.get(0);
        }
        UserContext userContext = new UserContext(name,address,port);
        int userid;
        synchronized (quemap) {
            while (true) {
                userid = random.nextInt(Integer.MAX_VALUE);
                if (!quemap.containsKey(userid)) {
                    break;
                }
            }
            userContext.setUserId(userid);
            quemap.put(userid, userContext);
        }
        putTran(name,userContext);
        return userContext;
    }
    public int putTran(String user,UserContext userContext){
        synchronized (tranmap) {
            ArrayList<UserContext> userContexts = tranmap.get(user);
            if (userContexts != null) {
                boolean exist=false;
                for (int i=0;i<userContexts.size();i++){
                    UserContext userContext1=userContexts.get(i);
                    if ((userContext.inetAddress.equals(userContext1.inetAddress)) &&
                            (userContext.port != userContext1.port)&&
                            (userContext.getUserId() != userContext1.getUserId())
//                            && (userContext.getBothId() != userContext1.getBothId())
                    ){
                        userContexts.set(i,userContext);
                        exist=true;
                        break;
                    }
                }
                if(!exist){
                    userContexts.add(userContext);
                }
                return userContexts.size();
            } else {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(userContext);
                tranmap.put(user, arrayList);
                return arrayList.size();
            }
        }
    }
    /* 输入目的端口和地址*/
    public UserContext createServerContext(InetAddress address, Integer port) {
        String name="server";
        Integer integer = usermap.get(name);
        UserContext userContext;
        if (integer!=null) {
            userContext=quemap.get(integer);
            if ((!userContext.inetAddress.equals(address))||(userContext.port!=port)){
                userContext.inetAddress=address;
                userContext.port=port;
                return userContext;
            }else {
                return userContext;
            }
        }else {
            userContext = new UserContext(name,address,port);
        }
        userContext.setQueue((short) 0,new ServerQueue(50));
        synchronized (quemap) {
            quemap.put(0, userContext);
            usermap.put(name, 0);
        }
        return userContext;
    }

    /* 输入目的端口和地址*/
    public UserContext createSelfContext(InetAddress address, Integer port) {
        String name=UDPclient.userlocal.username;
        Integer integer = usermap.get(name);
        UserContext userContext;
        if (integer!=null) {
            userContext=quemap.get(integer);
            if ((!userContext.inetAddress.equals(address))||(userContext.port!=port)){
                userContext.inetAddress=address;
                userContext.port=port;
                return userContext;
            }else {
                return userContext;
            }
        }else {
            userContext = new UserContext(name,address,port);
        }
        int userid;
        synchronized (quemap) {
            while (true) {
                userid = random.nextInt(Integer.MAX_VALUE);
                if (quemap.containsKey(userid)) {
                    continue;
                } else {
                    break;
                }
            }
            userContext.setUserId(userid);
            quemap.put(userid, userContext);
            usermap.put(name, userid);
        }
        return userContext;
    }

    public Map<Short, ByteBufer> getQueUser(String username) {
        User name = UDPclient.bindUser.get(username);
        try {
            username = name.username;
        } catch (Exception e) {
            System.out.println("未建立连接");
            return null;
        }
        Integer userid = usermap.get(username);
        UserContext userContext = quemap.get(userid);
        return userContext.map;
    }

    public UserContext getQueUser(Integer userid) {
        return quemap.get(userid);
    }

    public Map<Short,ByteBufer> getQueMap(Integer userid) {
        if (!quemap.containsKey(userid)) {
            System.out.println("未建立连接");
            return null;
        }
        UserContext userContext = quemap.get(userid);
        return userContext.map;
    }


    //从上下文环境获取
    //从上下文环境获取
    public UserContext contrainUser(String username){
        Integer userid = usermap.get(username);
        if (userid==null){
            return null;
        }
        UserContext userContext = quemap.get(userid);
        if (userContext==null){
            return null;
        }else {
            return userContext;
        }
    }
    public UserContext getUserContext(String username){
        try {
            Integer userid = usermap.get(username);
            return quemap.get(userid);
        }catch (Exception e){
            return null;
        }

    }
    public UserContext getUserContext1(String username) throws Exception{
        Integer userid = usermap.get(username);
        Object userContext = quemap.get(userid);
        if (userContext==null){
            throw new Exception("不存在用户");
        }else {
            return (UserContext)userContext;
        }
    }

    //从上下文环境获取
    public UserContext getUserContext(Integer userid) {
        UserContext userContext = quemap.get(userid);
        return userContext;
    }

    public Map<Short, ByteBufer> getQueServer() {
        Map<Short, ByteBufer> map = quemap.get(0).map;
        return map;
    }

    //添加接收队列
    public boolean addtask(String name, Short id) {
        Map<Short, ByteBufer> queueMap = getQueUser(name);
        queueMap.put(id, new ByteQueLink(1000));
        return true;
    }

    public boolean addtask(String name, Short id, int longs) {
        Map<Short, ByteBufer> queueMap = getQueUser(name);
        queueMap.put(id, new ByteQueLink(longs));
        return true;
    }

    public synchronized boolean deltask(Integer id) {
//        System.out.println("deltask(String username,Integer id)  " + id);
//
//        UserContext userContext = quemap.get(id);
//        userContext.finalize();
//        quemap.remove(id);
//        usermap.remove(userContext.user.username,id);
//        System.gc();
        return true;
    }

    public static UserContext callBack(UserContext userContext){
        System.out.println("CallBack   :");
        System.out.println("CallBackU   :  "+userContext);
        return null;
    }
    public UserContext delUser(String usename) throws Exception {
        if(UDPclient.userlocal.username.equals(usename)){
            return null;
        }
        UserContext userContext =getUserContext(usename);
        if(userContext!=null){
            quemap.remove(userContext.getUserId());
            usermap.remove(usename,userContext.getUserId());
            ipportmap.remove(userContext.inetAddress+":"+userContext.port);
            userContext.over();
        }
        BindFactory.bindrecs.remove(usename);
        System.gc();
        UDPclient.delUser1(usename);
        return userContext;
    }

    //增加接收线程数量
    public void addspead(MainReciverques reciverques) {
        mainthreadPoolExecutor.execute(reciverques);
    }

//    public int newId(UserRequest userRequest) {
//        Integer id;
//        BlockingQueue<byte[]> blockingQueue = new LinkedBlockingQueue<byte[]>(1000);
//        Map<Integer, BlockingQueue<byte[]>> map = new ConcurrentHashMap<Integer, BlockingQueue<byte[]>>();
//        map.put(0, blockingQueue);
//        synchronized (lock) {
//            while (true) {
//                id = random.nextInt(99);
//                if (quemap.containsKey(id)) {
//                    return id;
//                } else {
//                    UserContext userContext = new UserContext(userRequest);
//                    usermap.put(userRequest.username, id);
//                    quemap.put(id, userContext);
//                    //userid+00que
//                    break;
//                }
//            }
//        }
//        return id;
//    }



    public int getUserId(String username) {
        return usermap.get(username);
    }

    @Override
    public void finalize() throws Throwable {
        System.out.println(" mainthreadPoolExecutor.shutdown()");
        mainthreadPoolExecutor.shutdown();
    }


}
