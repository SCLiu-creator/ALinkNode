package superlink.udpbind.client.recives.data.blockBuffer;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DataStRead implements ByteBufer {

    public short wShort;
    public short id;
    public int fakeputIndex;
    public int putIndex;
    public int takeIndex;
    public volatile int count;

    public boolean over;

    public UserContext userContext;
    public Senders senders;
    public int time0;

    public byte[][] items;
    int len=32;
    //num 256*256
    //st 256

    public DataStRead(UserContext userContext,int id){
        this.userContext=userContext;
        this.id= (short) id;
        senders=new Senders();
        senders.InitInit(this.id,userContext);
        items=new byte[len][];
//        inetAddress=userContext.inetAddress;
//        port=userContext.port;
    }

    public void setTime0(int time0) {
        this.time0 = time0;
    }

    private void enqueue(byte[] x) {
        Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length){
            putIndex = 0;
        }
        count++;
        this.notify();
    }
    private byte[] dequeue() {
        byte[][] items = this.items;
        byte[] x = items[takeIndex];
        if (x==null){
            return null;
        }
        items[takeIndex] = null;
        if (x[3]!=ab){
            return null;
        }

        if (++takeIndex == items.length){
            ab= (byte) (0-ab);
            takeIndex = 0;
        }

        count--;
//        try {
//            this.notify();
//        }  catch (IllegalMonitorStateException e){
//
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
        return x;
    }

    public boolean comparePosRead(int i){
        if (putIndex==takeIndex){
            if (count==32){
                return false;
            }else {
                return true;
            }
        }
        if (putIndex>takeIndex){
            if (i>=putIndex){
                return true;
            }else {
                if (i<=takeIndex){
                    return true;
                }else {
                    return false;
                }
            }
        }else {
            if (i<=takeIndex){
                if (i>=putIndex){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }
    }
    public boolean comparePosAdd1(int i) {
        if(putIndex==takeIndex){
            if (count==0){
                return true;
            }else {
                return false;
            }
        }
        if (putIndex > takeIndex) {
            // 环绕情况: i < takeIndex 或 i >= putIndex
            return i < takeIndex || i >= putIndex;
        } else {
            // 非环绕情况: putIndex <= i <= takeIndex
//            return putIndex <= i && i <= takeIndex;
            return putIndex <= i && i < takeIndex;
        }
    }
    byte ab=1;
    //if length==0 over
    public synchronized byte[] read(){
        while (true){
            while(count==0 || items[takeIndex]==null){
//            synchronized (this){
                if (items[takeIndex]!=null){
                    count++;
                    System.out.println("rt :"+takeIndex +" c: "+count);
                    continue;
                }
//            }

//            if (fakeputIndex>putIndex){
////                    if (fakeputIndex>putIndex){
//                        for (int i = putIndex, j=fakeputIndex; i < j; i++) {
//                            senders.send(Utils.shortToByteArray((short) i),new byte[]{-1,ab});
//                        }
////                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException interruptedException) {
//                        interruptedException.printStackTrace();
//                    }
//            }
                senders.send(Utils.shortToByteArray((short) takeIndex),new byte[]{-1,ab});
                try {
                    this.wait(time0*10);
                }catch (InterruptedException | IllegalMonitorStateException ie){

                }catch (Exception e){
                    e.printStackTrace();
                }
//            try {
//                this.wait(time0);
//            }catch (InterruptedException | IllegalMonitorStateException ie){
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            }
            System.out.println("take :"+takeIndex);
            byte[] bytes=dequeue();
            if (bytes==null){
                continue;
            }
            senders.send(new byte[]{bytes[0],bytes[1],0,bytes[3]});
            int l = (int) Utils.calculateChecksum(bytes, 4, bytes.length - 8);
            if (l != Utils.byteArrayToInt(bytes, bytes.length - 4)) {
               continue;
            }
            return bytes;
        }
    }

    @Override
    public synchronized void add(DatagramPacket packet) {
        byte[] bytes=packet.getData();
        short s= Utils.byteArrayToshort(bytes,6);
        if (comparePosRead(s)){
            if (bytes[9]==ab){
                if (s!=putIndex){
                    fakeputIndex=s;
                    if (items[s]==null){
                        items[s]= Arrays.copyOfRange(packet.getData(), 6, packet.getLength());
                    }
                }else {
                    if (items[s]==null){
                        items[putIndex]= Arrays.copyOfRange(packet.getData(), 6, packet.getLength());
                        if (++putIndex == items.length){
                            putIndex = 0;
                        }
                        count++;
                    }
                }
            }
            try {
                this.notify();
            }  catch (IllegalMonitorStateException e){

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public boolean add(byte[] e) {
        return false;
    }

    @Override
    public byte[] poll() {
        return new byte[0];
    }

    @Override
    public byte[] take() throws InterruptedException {
        return new byte[0];
    }

    @Override
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        return new byte[0];
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
