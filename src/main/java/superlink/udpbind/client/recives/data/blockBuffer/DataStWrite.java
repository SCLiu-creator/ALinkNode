package superlink.udpbind.client.recives.data.blockBuffer;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DataStWrite implements ByteBufer {

    public short wShort;
    public short id;
    public int putIndex;
    public int takeIndex;
    public int count;

    public boolean over;

    public UserContext userContext;

    public ByteBufer blockingQueue;

    public byte[][] items=new byte[32][];
    public InetAddress inetAddress;
    int port;
    //num 256*256
    //st 256

    public DataStWrite(UserContext userContext,int id){
        this.userContext=userContext;
        blockingQueue=userContext.getDataQue(this.id);
        this.id= (short) id;
        inetAddress=userContext.inetAddress;
        port=userContext.port;
        prex= Utils.getUseridByte(userContext.getBothId(), (short) id);;
    }

    private synchronized void enqueue(byte[] x) {
        Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length){
            ab= (byte) (0-ab);
            putIndex = 0;
        }
        count++;
        try {
            this.notify();
        }catch (IllegalMonitorStateException e){

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
//        System.out.println("hash: "+ Arrays.hashCode(x));

    }
    private byte[] dequeue() {
        byte[][] items = this.items;
        byte[] x = items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length){
            takeIndex = 0;
        }
        count--;
        this.notify();
        return x;
    }


    public boolean comparePosRead(int i){
        if (putIndex>takeIndex){
            if (i<putIndex && i>=takeIndex){
                return true;
            }else {
                return false;
//                if (i<=takeIndex){
//                    return true;
//                }else {
//                    return false;
//                }
            }
        }else {
            if (i>=takeIndex || i<putIndex){
                return true;
            }else {
                return false;
            }
        }
    }
    public boolean comparePosRead0(int i){
        if (putIndex>=takeIndex){
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
                return true;
            }else {
                return false;
            }
        }
    }
    public boolean comparePosClear(int i){
        if (putIndex==takeIndex){
            if(count>0){
                return true;
            }else {
                return false;
            }
        }

        if (putIndex>takeIndex){
            if (i>=takeIndex&& i<putIndex){
                return true;
            }else {
                return false;
//                if (i<putIndex){
//                    return true;
//                }else {
//                    if (count>0){
//                        return true;
//                    }else {
//                        return false;
//                    }
//                }
            }
        }else {
            if (i>=takeIndex || i<putIndex){
                return true;
            }else {
//                if (i>=takeIndex){
//                    return true;
//                }else {
//                    return false;
//                }
                return false;
            }
        }
    }
    public boolean comparePosClear0(int i){
        if (putIndex>=takeIndex){
            if (i>=takeIndex){
                if (i<putIndex){
                    return true;
                }else {
                    if (count>0){
                        return true;
                    }else {
                        return false;
                    }
                }
            }else {
                return false;
            }
        }else {
            if (i<putIndex){
                return true;
            }else {
                if (i>=takeIndex){
                    return true;
                }else {
                    return false;
                }
            }
        }
    }

    public void write(byte[] bytes){

        while (count == items.length){
            try {
//                Thread.sleep(1000);
                this.wait(1000);
            } catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        enqueue(bytes);
    }
    public synchronized void over(){
        byte[] bytes=Utils.byteMerger(prex,Utils.shortToByteArray((short) putIndex),new byte[]{0,ab});
        while (count == items.length){
            try {
                this.wait(1000);
            } catch (IllegalMonitorStateException interruptedException) {

            }catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
            while (blockingQueue.size()>0){
                short s=Utils.byteArrayToshort(blockingQueue.poll());
                if(comparePosRead(s)){
                    if (items[s]!=null){
                        try {
                            Senders.Sends(userContext.inetAddress,userContext.port,items[s]);
                        }catch (Exception e){ }
                    }
                }
            }
        }
        Senders.Sends(inetAddress,port,bytes);
        enqueue(bytes);
        while (count!=0){
            try {
                this.wait(1000);
            } catch (IllegalMonitorStateException interruptedException) {

            }catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
            while (blockingQueue.size()>0){
                short s=Utils.byteArrayToshort(blockingQueue.poll());
                if(comparePosRead(s)){
                    if (items[s]!=null){
                        try {
                            Senders.Sends(userContext.inetAddress,userContext.port,items[s]);
                        }catch (Exception e){ }
                    }
                }
            }
        }

    }
    byte[] prex;
    byte ab=1;
    public void write(byte[] bytes,int i){
        byte[] bytes1=Utils.subByte(bytes,0,i);
        int ca=(int)Utils.calculateChecksum(bytes1);
        bytes=Utils.byteMerger(prex,Utils.shortToByteArray((short) putIndex),new byte[]{0,ab},bytes1,Utils.intToByteArray(ca));
        while (count == items.length){
            try {
                this.getClass().wait(4000);
            } catch (IllegalMonitorStateException interruptedException) {

            }catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
            while (blockingQueue.size()>0){
                short s=Utils.byteArrayToshort(blockingQueue.poll());
                if(comparePosRead(s)){
                    if (items[s]!=null){
                        try {
                            Senders.Sends(userContext.inetAddress,userContext.port,items[s]);
                        }catch (Exception e){ }
                    }
                }
            }
            try {
                Senders.Sends(userContext.inetAddress,userContext.port,items[takeIndex]);
            }catch (Exception e){ }

        }
        Senders.Sends(inetAddress,port,bytes);
        enqueue(bytes);
    }

    @Override
    public synchronized void add(DatagramPacket packet) {
        byte[] bytes=packet.getData();
        short s= Utils.byteArrayToshort(bytes,6);
        if (bytes[8]<0){
            blockingQueue.add(Arrays.copyOfRange(packet.getData(), 6, packet.getLength()));
        }else {
            if (comparePosClear(s)){
                if (bytes[9]==items[s][9]){
//                    s;
//                    if (s == items.length){
//                        s=0;
//                    }
                    int  co=count;

                    if (s>=takeIndex && s<putIndex){
                        for (int i = takeIndex; i < s; i++) {
                            items[i]=null;
                        }
                        count=count-(s-takeIndex);
                    }else {
                        if(s>=takeIndex){
                            for (int i = takeIndex; i < s; i++) {
                                items[i]=null;
                            }
//                            takeIndex=s;
                            count=count-(s-takeIndex);
                        }else {
                            for (int i = 0; i < s; i++) {
                                items[i]=null;
                            }
                            for (int i = takeIndex; i < items.length-1; i++) {
                                items[i]=null;
                            }
                            co=items.length-takeIndex+s;
                            count=count-co;
                        }
                    }
                    takeIndex=s;
//                    if (s<takeIndex){
//                        co=items.length-takeIndex+s;
//                    }else {
//                        co=s-takeIndex;
//                    }
//                    takeIndex=s;
//                    count=count-co;
                }

                try {
                    this.getClass().notify();
                } catch (IllegalMonitorStateException interruptedException) {

                }catch (Exception interruptedException) {
                    interruptedException.printStackTrace();
                }

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

    public static void main(String[] args) {

    }
}
