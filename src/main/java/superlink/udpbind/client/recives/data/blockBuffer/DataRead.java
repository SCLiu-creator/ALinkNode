package superlink.udpbind.client.recives.data.blockBuffer;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

//淘汰
public class DataRead implements ByteBufer {
    //todo
    public volatile short rShort=Short.MIN_VALUE;
    public short wShort;
    public short id;
    public UserContext userContext;

    public byte[][] bytess=new byte[Short.MAX_VALUE-Short.MIN_VALUE][];

    //short for num
    //short for order
    @Override
    public void add(DatagramPacket packet) {
        short num=Utils.byteArrayToshort(packet.getData(),6);
        synchronized (this){
            if (num>=rShort){
                bytess[rShort-Short.MIN_VALUE]= Arrays.copyOfRange(packet.getData(), 6, packet.getLength());
            }
        }
        bytess.notify();
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
        byte[] bytes=bytess[rShort];
        while (bytes==null){
            Senders.Sends(userContext.userName,id,Utils.byteMerger(Utils.shortToByteArray(rShort),new byte[2]));
            try {
                bytess.wait(3000);
            }catch (Exception e){

            }
            bytes=bytess[rShort];
        }

        bytess[rShort]=null;
        rShort++;
        return bytes;

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
