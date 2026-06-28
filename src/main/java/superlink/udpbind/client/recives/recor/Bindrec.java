package superlink.udpbind.client.recives.recor;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteReBuffer;
import superlink.udpbind.client.recives.ByteRsBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeUnit;

public class Bindrec implements irec ,Runnable{

    public ByteBufer blockingQueue;
    public ByteRsBuffer rsBufer;
    public ByteReBuffer reBufer;
    public String username;
    public Deals deals;
    public symmetry symmetry;
    public Bindrec(ByteBufer blockingQueue, String username){
        UserContext userContext= UDPclient.getUser(username);
        rsBufer= (ByteRsBuffer) userContext.getQueue((short)5);
        reBufer= (ByteReBuffer) userContext.getQueue((short)7);
        this.blockingQueue=blockingQueue;
        this.username=username;
        this.deals=new Deals(username);
    }
    public void Bindrec(ByteBufer blockingQueue,String username){
        UserContext userContext= UDPclient.getUser(username);
        rsBufer= (ByteRsBuffer) userContext.getQueue((short)5);
        rsBufer.setUser(userContext);
        reBufer= (ByteReBuffer) userContext.getQueue((short)7);
        rsBufer.setUser(userContext);
        this.blockingQueue=blockingQueue;
        this.deals=new Deals(username);
        time=System.currentTimeMillis();
        t=0;
    }
    @Override
    public void run() {
        Thread.currentThread().setName("bindrec: "+username);
        int t=0;
        byte[] prex=("LL"+ UDPclient.userlocal.username).getBytes();
        while (true){
            byte[] bytes=null;//"LL".getBytes();
            try {
                bytes=blockingQueue.poll(10, TimeUnit.SECONDS);
//                if (bytes.length>30){
//                  //  System.out.println("blockingQueue: "+new String(bytes,0,30));
//                }else {
//                   // System.out.println("blockingQueue: "+new String(bytes));
//                }
                deals.setRequest(bytes).deal();

                t=0;
            }catch (Exception e){
                if (t>5){
                    try {
                        UDPclient.mainDataQueue.delUser(username);
                        System.out.println("超时，回收线程:"+username);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                }else {
                    UserContext userContext= null;
                    try {
                        userContext = UDPclient.mainDataQueue.getUserContext(username);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    Integer userid=userContext.getBothId();
                    bytes= Utils.byteMerger(Utils.getUseridByte(userid, (short) 0),prex);
                    Senders.Sends2(userContext.inetAddress,userContext.port,bytes);
                    t++;
                }
                e.printStackTrace();
            }
        }
    }



    int t=0;
    int size;
    public void unblockrun() {
        try {
            byte[] bytes=blockingQueue.poll();
            deals.setRequest(bytes);
            deals.deal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int overTimes=10;
    public long time=System.currentTimeMillis();
    public void reLink(){
        long tl=System.currentTimeMillis()-time;
        if (tl>8000){
            time=System.currentTimeMillis();
            t++;
        }else {
            return;
        }
        if(deals.i>0){
            t=0;
        }
        if (t%2!=0 || t==0){
            return;
        }
//        if (t>overTimes){
//            try {
//                System.out.println("超时，断开用户:"+username);
//                UDPclient.mainDataQueue.delUser(username);
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//        }else {
//            UserContext userContext = deals.Deals(username);
//            if(userContext==null)return;
//            int userid=userContext.getBothId();
//            byte[] bytes= Utils.byteMerger(Utils.getUseridByte(userid, (short) 0),("LL"+ UDPclient.userlocal.username).getBytes());
//                Senders.Sends2(userContext.inetAddress,userContext.port,bytes);
//                System.out.println("reLink:  "+username+"  "+t+" mode: "+userContext.sort);
////            time=System.currentTimeMillis();
//        }
    }


    public int unblockrun(int i) {
        byte[] bytes=null;//"LL".getBytes();
        try {
            bytes=blockingQueue.poll();
            deals.setRequest(bytes);
            deals.deal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockingQueue.size()+i;
    }

    class symmetry{
        int idSelf;
        int idBoth;
        String me;
        String it;

        public int getIdSelf() {
            return idSelf;
        }

        public void setIdSelf(int idSelf) {
            this.idSelf = idSelf;
        }

        public int getIdBoth() {
            return idBoth;
        }

        public void setIdBoth(int idBoth) {
            this.idBoth = idBoth;
        }

        public String getMe() {
            return me;
        }

        public void setMe(String me) {
            this.me = me;
        }

        public String getIt() {
            return it;
        }

        public void setIt(String it) {
            this.it = it;
        }
    }

}
