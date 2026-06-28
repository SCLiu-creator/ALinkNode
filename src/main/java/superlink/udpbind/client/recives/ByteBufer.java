package superlink.udpbind.client.recives;

import java.net.DatagramPacket;
import java.util.concurrent.TimeUnit;

public interface ByteBufer {
    public void add(DatagramPacket packet);
    public boolean add(byte[] e);
//    public boolean offer(byte[] e);
//    public boolean offer(byte[] o, long timeout, TimeUnit unit) throws InterruptedException;
//    public void put(byte[] e) throws InterruptedException;
    public byte[] poll();
    public byte[] take() throws InterruptedException;
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException;
    public int size();
    public void clear();
}
