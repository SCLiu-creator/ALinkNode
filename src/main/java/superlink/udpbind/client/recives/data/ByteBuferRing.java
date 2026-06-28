package superlink.udpbind.client.recives.data;

import superlink.udpbind.client.recives.ByteBufer;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class ByteBuferRing implements ByteBufer {

    public Con[] cons;

    public ByteBuferRing(int i){
        cons=new Con[i];
        cons[0]=new Con();
        for (int j = 1; j <cons.length ; j++) {
            cons[j]=new Con();
            cons[j].setLast(cons[j-1]);
        }
        cons[0].setLast(cons[i-1]);
    }

    @Override
    public void add(DatagramPacket packet) {

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

    public class Con{
        Con last;
        Con next;
        byte[] bytes;

        public Con getLast() {
            return last;
        }

        public void setLast(Con last) {
            this.last = last;
            last.next=this;
        }

        public Con getNext() {
            return next;
        }

        public void setNext(Con next) {
            this.next = next;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            return String.valueOf(Utils.byteArrayToInt(bytes)) ;
        }
    }

    public static void main(String[] args) {
        ConcurrentHashMap concurrentHashMap=new ConcurrentHashMap();
        Short ss=-1234;
        Integer is=-1234;
        int h1=ss.hashCode();
        int h2=is.hashCode();
        byte[] b1=Utils.intToByteArray(ss);
        byte[] b2=Utils.shortToByteArray(ss);
        ConcurrentSkipListSet hashSet;
//        hashSet.add()

        ByteBuferRing byteBuferStream=new ByteBuferRing(25);
        Object o=concurrentHashMap.remove(1);
        concurrentHashMap.put(2,byteBuferStream);
        o=concurrentHashMap.remove(2);
//        for (Con con:byteBuferStream.cons){
//            System.out.println(con);
//        }
        Con con=byteBuferStream.cons[0];
        Integer i=0;
        while (con!=null){
            con.bytes= Utils.intToByteArray(i);
            i++;
            System.out.println(con);
            con=con.getNext();
        }
        ;
    }
}
