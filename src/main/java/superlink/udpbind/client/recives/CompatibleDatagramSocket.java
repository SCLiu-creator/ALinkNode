package superlink.udpbind.client.recives;

import sun.nio.ch.IOStatus;
import sun.nio.ch.Interruptible;
import sun.nio.ch.Net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

public class CompatibleDatagramSocket extends DatagramSocket {
    private  DatagramChannel channel;
    private  DatagramSocket socket;
    public CompatibleDatagramSocket(DatagramChannel bindchannel) throws SocketException {
        channel=bindchannel;
        try {
            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(DatagramPacket packet) throws IOException {
        // 方法1：尝试直接使用 socket.receive(packet)
        Thread.interrupted();
        try {
            synchronized(packet) {
                ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
                InetSocketAddress var5 = null;
//                while (true){
                    try{
                        var5= (InetSocketAddress) channel.receive(buffer);
                    } catch (ClosedChannelException e) {
//                        channel.close();
                        e.printStackTrace(); // 查看关闭来源
                    }
                    if(var5!=null){
                        packet.setSocketAddress(var5);
                        int i1= buffer.position();
                        int i2= packet.getOffset();
                        packet.setLength(buffer.position() + packet.getOffset());
                        return;
                    }
//                    else {
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException interruptedException) {
//                            interruptedException.printStackTrace();
//                        }
//                    }
                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 方法2：如果失败，改用 channel.receive() + 手动填充 packet
//        ByteBuffer buffer = ByteBuffer.allocate(packet.getData().length);
//        channel.receive(buffer);
//        buffer.flip();
//        buffer.get(packet.getData(), packet.getOffset(), buffer.limit());
//        packet.setLength(buffer.limit());
    }

    public InetSocketAddress receive(ByteBuffer buffer) {
        // 方法1：尝试直接使用 socket.receive(packet)
//        boolean intd=false;
//        if(Thread.currentThread().isInterrupted()){
//            return null;
//        }
//        Thread.interrupted();
        try {
            InetSocketAddress var5 = null;
            try {
                var5 = (InetSocketAddress) channel.receive(buffer);
            } catch (ClosedChannelException e) {
//                        channel.close();
                e.printStackTrace(); // 查看关闭来源
            }
            if (var5 != null) {
                return var5;
            }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 其他必要方法（send, close 等）
    //被中断会抛出ClosedByInterruptException，并回调close方法。
    public void send(DatagramPacket packet) throws IOException {
        try {
            synchronized(packet) {
//                Thread.interrupted();
                ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
                if (packet.getAddress() == null) {
                    throw new IOException("null address");
                } else {
                    channel.send(buffer, packet.getSocketAddress());
                }
            }
        } catch (IOException var9) {
            var9.printStackTrace();
        }

//        channel.send(ByteBuffer.wrap(packet.getData()), packet.getSocketAddress());
    }
//    public int send(ByteBuffer var1, SocketAddress var2) throws IOException {
//        if (var1 == null) {
//            throw new NullPointerException();
//        } else {
//            synchronized(this.writeLock) {
//                this.ensureOpen();
//                InetSocketAddress var4 = Net.checkAddress(var2);
//                InetAddress var5 = var4.getAddress();
//                if (var5 == null) {
//                    throw new IOException("Target address not resolved");
//                } else {
//                    synchronized(this.stateLock) {
//                        if (this.isConnected()) {
//                            if (!var2.equals(this.remoteAddress)) {
//                                throw new IllegalArgumentException("Connected address not equal to target address");
//                            }
//
//                            int var10000 = this.write(var1);
//                            return var10000;
//                        }
//
//                        if (var2 == null) {
//                            throw new NullPointerException();
//                        }
//
//                        SecurityManager var7 = System.getSecurityManager();
//                        if (var7 != null) {
//                            if (var5.isMulticastAddress()) {
//                                var7.checkMulticast(var5);
//                            } else {
//                                var7.checkConnect(var5.getHostAddress(), var4.getPort());
//                            }
//                        }
//                    }
//
//                    int var6 = 0;
//
//                    byte var20;
//                    try {
//                        this.begin();
//                        if (this.isOpen()) {
//                            this.writerThread = NativeThread.current();
//
//                            do {
//                                var6 = this.send(this.fd, var1, var4);
//                            } while(var6 == -3 && this.isOpen());
//
//                            synchronized(this.stateLock) {
//                                if (this.isOpen() && this.localAddress == null) {
//                                    this.localAddress = Net.localAddress(this.fd);
//                                }
//                            }
//
//                            int var21 = IOStatus.normalize(var6);
//                            return var21;
//                        }
//
//                        var20 = 0;
//                    } finally {
//                        this.writerThread = 0L;
//                        this.end(var6 > 0 || var6 == -2);
//
//                        assert IOStatus.check(var6);
//
//                    }
//
//                    return var20;
//                }
//            }
//        }
//    }
//
//    protected final void begin() {
//        if (interruptor == null) {
//            interruptor = new Interruptible() {
//                public void interrupt(Thread target) {
//                    synchronized (closeLock) {
//                        if (!open)
//                            return;
//                        open = false;
//                        interrupted = target;
//                        try {
//                            AbstractInterruptibleChannel.this.implCloseChannel();
//                        } catch (IOException x) { }
//                    }
//                }};
//        }
//        blockedOn(interruptor);
//        Thread me = Thread.currentThread();
//        if (me.isInterrupted())
//            interruptor.interrupt(me);
//    }
}
