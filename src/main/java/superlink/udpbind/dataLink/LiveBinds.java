package superlink.udpbind.dataLink;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.udpbind.handle.Handler.removeUdp;

public class LiveBinds implements Runnable{
    public UserRequest userRequest;
    public volatile boolean control=false;
    public UdpData udpData;

    public final ReentrantLock pauseLock = new ReentrantLock();

    public BlockingQueue<byte[]> queue;
    private  int state=0;
    private boolean sym=true;
    private DatagramPacket packet;

    public LiveBinds(UserRequest userRequest){
        this.userRequest=userRequest;
        System.out.println("LIVE UserRequest:"+ JSON.toJSONString(userRequest));
        //this.queue=ReciveQueueFactory.getQueMap(userRequest.username,0);
        this.queue=  new ArrayBlockingQueue<>(3);
        byte[] bytes= Utils.byteMerger(new byte[]{0},"LL".getBytes());
        this.packet=new DatagramPacket(bytes,bytes.length,userRequest.toaddress,userRequest.toport);
    }
    // 暂停方法，把暂停标志设为true
    public void stoplive() {
        control = true;
//        LiveBind liveBind=(LiveBind) Handler.liveMap.get(userRequest.username);
//        liveBind.startlive();
//        try {
//            udpData.dataSocket.send(new DatagramPacket("LL".getBytes(),0,"LL".getBytes().length,liveBind.userRequest.toaddress,liveBind.userRequest.toport));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // 恢复方法，把暂停标志设为false，并通知锁对象
    public void startlive(){
        synchronized (pauseLock) {
            control = false;
            pauseLock.notifyAll();
        }
    }


    @Override
    public void run(){
        udpData=Handler.UdpMap.get(userRequest.username);
        DatagramSocket socket=udpData.dataSocket;
        try {
            Thread.sleep(1000);
            socket.send(packet);
        } catch (IOException |InterruptedException e) {
            e.printStackTrace();
        }
        byte[] bytes=new byte[512];
        //DatagramPacket repacket=new DatagramPacket(bytes,bytes.length);
        String s = null;
        while (sym){
            synchronized (pauseLock) {
                if (Thread.interrupted()){
                    break;
                }
//                synchronized (lock){
//                    try {
//                        lock.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }}
                while (control) {//线程中断
                    try {
                        pauseLock.wait();
                    }catch (InterruptedException e) {
                        // 如果线程被中断，打印异常信息，并重新设置中断状态
                        Thread.currentThread().interrupt();
                        System.out.println("Thread was interrupted, Failed to complete operation");
                    }
                }
                try {

                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    overUdp(userRequest);
                    e.printStackTrace();
                }
                // socket.send(packet);

                try {
                    byte[] bytes1= queue.poll(3, TimeUnit.MINUTES);
                    s = new String(bytes1);
                    if (s.equals("LL")) {
                        System.out.println("LL");
                        try {
                            socket.send(packet);
                            udpData.bindsrec.setZero();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException | NullPointerException e) {
                    //Controller.udpDataLink(Controller.chooseUserRequest(udpData.userRequest.username));
                    state++;
                    e.printStackTrace();

                    if (state>1){overUdp(userRequest);break;}
                    sendLL();
                }

                //String s = new String(repacket.getData(), 0, repacket.getLength()).substring(4, 6);
                //repacket.setData(new byte[512]);

            }}

    }
    public void sendLL(){
        try {
            udpData.dataSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void over(){
        this.queue.add(new byte[1]);
        this.sym=false;
        // Controller.udpDataLink(request);

    }
    public static void overUdp(UserRequest request){
        removeUdp(request.username);
       // Controller.udpDataLink(request);

    }

}
