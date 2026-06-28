package superlink.udpbind.tcpproxy;

import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.data.datastream.DataStreamABb;
import superlink.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyServer {
    DataStreamAB streamAB;
    int port;
    Map<Byte, SocketChannel> socketMap=new HashMap<>();
    Map<SocketChannel, Byte> channelByteHashMap=new HashMap<>();
    Selector selector = null;
    public ProxyServer(int port, DataStreamAB streamAB){
        this.port=port;
        this.streamAB=streamAB;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedBlockingQueue<Runnable> writeQueue = new LinkedBlockingQueue();
    Set<Byte> list=new HashSet<>();
    boolean register=false;
    public void runrecive(){
        Thread thread=Thread.currentThread();
        thread.setName("ProxyServer recive");
        SocketChannel socketChannel;
        boolean icc;
        while (true){
            byte[] bytes=streamAB.recive();

            if (bytes!=null&&bytes.length>0){
                Byte byt = bytes[0];
                socketChannel=socketMap.get(byt);
                if (socketChannel==null){
                    if (bytes.length==1){
                        if (bytes[0]<0){
                            Byte b= (byte) (bytes[0]&0b01111111);
                            list.add(b);
                            continue;
                        }
                    }
                    icc=true;
                    while (icc){
                        try {
                            thread.isInterrupted();
                            socketChannel = SocketChannel.open();
                            socketChannel.connect(new InetSocketAddress(port));
                            socketMap.put(bytes[0],socketChannel);
                            channelByteHashMap.put(socketChannel,bytes[0]);
                            socketChannel.configureBlocking(false);
                            register=true;
                            socketChannel.register(selector,SelectionKey.OP_CONNECT |SelectionKey.OP_READ);
                            register=false;
                            icc=false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(120);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }
                }

                SocketChannel finalSocketChannel = socketChannel;
                writeQueue.add(() -> {
                    try {
                        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes,1,bytes.length-1);
                        finalSocketChannel.write(byteBuffer);
                    } catch (ClosedChannelException e) {
                        System.out.println("ProxyServer ClosedChannel: "+e.getMessage());
                        try {
                            removeChannel(byt);
                            removeChannel(finalSocketChannel);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }catch (IOException e) {
                        System.out.println("ProxyServer recive: "+e.getMessage());
                        Thread.interrupted();
                        try {
                            Thread.sleep(120);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }finally {
                        if(bytes.length>60){
                            System.out.println("ProxyServer recive: "+
                                    new String(Utils.subByte(bytes,0,30))+
                                    " ... "+
                                    new String(Utils.subByte(bytes,bytes.length-30,bytes.length)));
                        }else {
                            System.out.println("ProxyServer recive: "+new String(bytes));
                        }
                    }
                });
            }

        }
    }

    // 定义超时时间（30秒）
    private static final long INACTIVITY_TIMEOUT_MS = 60000;
    // 记录最后一次活动时间
    private long lastActivityTime = System.currentTimeMillis();
    public void runread(){
        Thread.currentThread().setName("ProxyServer runread");
        while (true){
            try {
                int readyChannels=selector.select(1000);
                Runnable task;
                while (writeQueue.size()>0) {
                    task = writeQueue.poll();
                    task.run();
                }
                if (readyChannels == 0) {
                    if (register){
                        Thread.sleep(100);
                    }
                    continue;
                }
//                if (readyChannels > 0) {
//                    lastActivityTime = System.currentTimeMillis();
//                } else {
//                    long currentTime = System.currentTimeMillis();
//                    if (currentTime - lastActivityTime >= INACTIVITY_TIMEOUT_MS) {
////                        System.out.println("30秒无活动，关闭所有连接...");
//                        System.out.println("30秒无活动，试读...");
////                        closeAllChannels(); // 自定义方法：关闭所有通道并清理Selector
//                        lastActivityTime = currentTime; // 重置计时（可选）
////                        continue;
//                    }
//                    if (register) {
//                        Thread.sleep(200);
//                    }
//                    continue;
//                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                byte[] b=new byte[1];
                if (list.size() != 0 ) {
                    Iterator<Byte> iterator1=list.iterator();
                    while (iterator1.hasNext()) {
                        Byte next =  iterator1.next();
//                        removeChannel(next);
                        list.remove(next);
                    }
                }
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if(key.isValid() &&key.isReadable()) {  //处理连接事件
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(streamAB.pageLen-3);
                        int bytesRead=0;
                        try {
                            while ((bytesRead=client.read(buffer))!=-1){
                                Byte aByte=channelByteHashMap.get(client);
                                if (aByte!=null){
                                    b[0]=aByte;
                                }else {
                                    break;
                                }
                                byte[] buf=Utils.subByte(buffer.array(),0,bytesRead);
                                buf=Utils.byteMerger(b,buf);
                                streamAB.send(buf);
//                                System.out.println("ProxyServer runread :\n"+new String(buf));
                                if(buf.length>100){
                                    if(Utils.canBeEncodedStrict(Utils.subByte(buf,0,30),"utf-8")){
                                        System.out.println("ProxyServer runread :\n"+
                                                new String(Utils.subByte(buf,0,50))+
                                                " ... "+
                                                new String(Utils.subByte(buf,buf.length-50,buf.length)));
                                    }else {
                                        System.out.println("ProxyServer runread :\n"+
                                                Arrays.toString(Utils.subByte(buf,0,50))+
                                                " ... "+
                                                Arrays.toString(Utils.subByte(buf,buf.length-50,buf.length)));
                                    }
                                }else {
                                    System.out.println("ProxyServer runread :\n"+new String(buf));
                                }
                                if (bytesRead==0){
                                    break;
                                }
                            }
                            if (bytesRead == -1) {
                                // 连接关闭
                                removeChannel(client);
                                continue;
                            } else if (bytesRead > 0) {
                                // 更新最后活动时间（因为收到了数据）
                                lastActivityTime = System.currentTimeMillis();
                                // 处理接收到的数据...
                            }
                        }catch (IOException io){
                            key.channel();
                            removeChannel(client);
                        }
                        if (bytesRead<0){
                            key.channel();
//                            removeChannel(client);
                        }
                    }else {
                        SocketChannel client = (SocketChannel) key.channel();
//                        removeChannel(client);
                        Thread.sleep(1);
                    }
                    iterator.remove();  //事件处理完毕，要记得清除
//                    if (key.isValid() && key.isConnectable()){ }else { }
                }
                Thread.sleep(30);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    private void closeAllChannels() throws IOException {
        for (SelectionKey key : selector.keys()) {
            if (key.channel().isOpen()) {
                key.channel().close();
            }
        }
        selector.selectedKeys().clear();
    }

    public void removeChannel(SocketChannel channel) throws IOException {
        if (!channel.isConnected()){

            channel.close();
            Byte abyte=channelByteHashMap.get(channel);
            System.out.println("ProxySocket  remove :  "+abyte);
            channelByteHashMap.remove(channel);
            socketMap.remove(abyte);
//            cl[0]= (byte) (abyte.byteValue()|0b10000000);
//            streamAB.send(cl);
        }
    }
    byte[] cl=new byte[1];
    public void removeChannel(Byte b) throws IOException {
        SocketChannel channel=socketMap.get(b);
        System.out.println("ProxySocket  remove :  "+b);
        if (channel!=null){
            channelByteHashMap.remove(channel);
            socketMap.remove(b);
            channel.close();
//            cl[0]= (byte) (b.byteValue()|0b10000000);
//            streamAB.send(cl);
        }
        }

}
