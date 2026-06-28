package superlink.udpbind.dataLink.rec;

import superlink.udpbind.dataLink.LiveBinds;
import superlink.udpbind.client.recives.recor.irec;
import superlink.udpbind.handle.Handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Bindsrec implements irec,Runnable{

    public BlockingQueue<byte[]> blockingQueue;
    public String username;
    public linksDeals deals;
    private int i=0;
    public boolean run;
    public Bindsrec(BlockingQueue<byte[]> blockingQueue, String username){
        this.blockingQueue=blockingQueue;
        this.username=username;
        this.deals=new linksDeals(username);
        this.run=true;
    }
    @Override
    public void run() {
        while (run){
            byte[] bytes=null;//"LL".getBytes();
            try {
                bytes=blockingQueue.poll(2, TimeUnit.MINUTES);
                System.out.println("Bindsrec blockingQueues: "+new String(bytes));
            }catch (InterruptedException |NullPointerException e){
                if (i>3){
                    Handler.removeUdp(username);
                    System.out.println("超时，回收线程");
                    break;
                }
                i++;
                LiveBinds liveBinds = (LiveBinds) Handler.liveMap.get(username);
                liveBinds.sendLL();
                System.out.println("超时");
                e.printStackTrace();
            }
            deals.deal(bytes);

        }
    }

    public void over(){
        this.run=false;
        blockingQueue.add(new byte[1]);
    }

    public void setZero(){
        i=0;
    }
}
