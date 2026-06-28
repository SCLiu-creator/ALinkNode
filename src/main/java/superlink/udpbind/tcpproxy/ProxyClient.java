package superlink.udpbind.tcpproxy;

import cn.hutool.core.collection.ConcurrentHashSet;
import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.data.datastream.DataStreamABb;
import superlink.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyClient {

    DataStreamAB streamAB;
    int port;
    Map<Byte, SocketChannel> socketMap=new HashMap<>();
    Map<SocketChannel, Byte> channelByteHashMap=new HashMap<>();
    ServerSocketChannel serverSocketChannel;
    Selector selector = null;
    public ProxyClient(int port, DataStreamAB streamAB){
        this.streamAB=streamAB;
        try {
            //创建ServerSocketChannel，-->> ServerSocket
            serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(false); //设置成非阻塞
            //开启selector,并注册accept事件
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LinkedBlockingQueue<Runnable> writeQueue = new LinkedBlockingQueue();

    Set<Byte> list=new ConcurrentHashSet<>();
    public void runrecive(long i){
        Thread thread=Thread.currentThread();
        thread.setName("ProxyClient recive");
        boolean icc;
        byte[] bytes=null;
        long t1=System.currentTimeMillis();
        while (true){
//            long t2=System.currentTimeMillis();
//            if (((t2-t1))>i){
//                break;
//            }else {
//                i=i-(t2-t1);
//            }
            bytes=streamAB.recive(i);

            if(bytes==null || bytes.length==0){
                break;
            }
//            System.out.println("ProxyClient recive: "+new String(bytes));
            if(bytes.length>100){
                if(Utils.canBeEncodedStrict(Utils.subByte(bytes,0,30),"utf-8")){
                    System.out.println("ProxyClient recive: "+
                            new String(Utils.subByte(bytes,0,50))+
                            " ... "+
                            new String(Utils.subByte(bytes,bytes.length-50,bytes.length)));
                }else {
                    System.out.println("ProxyClient recive: "+
                            Arrays.toString(Utils.subByte(bytes,0,50))+
                            " ... "+
                            Arrays.toString(Utils.subByte(bytes,bytes.length-50,bytes.length)));
                }
            }else {
                System.out.println("ProxyClient recive: "+new String(bytes));
            }
//            try {
//                String gbkStr = new String(bytes, "GBK");
//                String isoStr = new String(bytes, StandardCharsets.ISO_8859_1);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            Byte sb = bytes[0];
            SocketChannel socket=socketMap.get(sb);
            if (socket==null){
                continue;
            }

            if (bytes.length==1){
                if (bytes[0]<0){
                    byte b= (byte) (bytes[0]&0b01111111);
                    if (!socket.isConnected()){
                        list.add(b);
                    }
                    continue;
                }
            }

            ByteBuffer buffer = ByteBuffer.wrap(bytes, 1, bytes.length - 1);
            writeQueue.add(() -> {
                try {
                    socket.write(buffer);
                } catch (ClosedChannelException e) {
                    System.out.println("ProxyClient ClosedChannel: "+e.getMessage());

                    try {
                        removeChannel(socket);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }catch (IOException e) {
                    if (!socket.isConnected()){
                    }
                    System.out.println(e.getMessage());
                    Thread.interrupted();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//                    ByteBuffer byteBuffer=ByteBuffer.wrap(bytes,1,bytes.length-1);
//                    socket.write(byteBuffer);
        }
    }


    public void runread(){
        Thread.currentThread().setName("ProxyClient runread");
        byte[] b=new byte[1];
        while(true) {
            try {
                 //监听所有通道
                int readyChannels=selector.select(1000);
                Runnable task;
                while (writeQueue.size()>0) {
                    task = writeQueue.poll();
                    task.run();
                }
//                if (readyChannels == 0) {
//                    continue;};
                //遍历selectionKeys
                runrecive(120);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                if (list.size() != 0 ) {
                    Iterator<Byte> iterator1=list.iterator();
                    while (iterator1.hasNext()) {
                        Byte next =  iterator1.next();
//                        streamAB.send(new byte[next]);
                        iterator1.remove();
//                        removeChannel(next);
//                        list.remove(next);
                    }
                }

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()) {  //处理连接事件
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);  //设置为非阻塞
                        System.out.println("web client:" + socketChannel.getLocalAddress() + " is connect");
                        Byte aByte=channelByteHashMap.get(socketChannel);
                        if (aByte==null){
                            for (int j = 0; j <127&&aByte==null ; j++) {
                                Random random=new Random();
                                Byte bs= (byte)random.nextInt(127);
                                if (!socketMap.containsKey(bs)){
                                    aByte=bs;
                                }
                            }
                        }
                        channelByteHashMap.put(socketChannel,aByte);
                        socketMap.put(aByte,socketChannel);
                        socketChannel.register(selector, SelectionKey.OP_READ); //注册客户端读取事件到selector
                    } else if (key.isReadable()) {  //处理读取事件
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(streamAB.pageLen-3);
                        int i=0;
                        try {
                            while ((i=client.read(buffer))!=-1){
                                Byte aByte=channelByteHashMap.get(client);
                                if (aByte!=null){
                                    b[0]=aByte;
                                }else {break;}
                                byte[] buf=Utils.subByte(buffer.array(),0,i);
                                buf=Utils.byteMerger(b,buf);
                                streamAB.send(buf);
                                if(buf.length>100){
                                    System.out.println("ProxyClient runread :\n"+
                                            new String(Utils.subByte(buf,0,50))+
                                            " ... "+
                                            new String(Utils.subByte(buf,buf.length-50,buf.length)));
                                }else {
                                    System.out.println("ProxyClient runread :\n"+new String(buf));
                                }
                                if (i==0){
                                    break;
                                }
                            }
                        }catch (Exception io){
                            System.out.println("ProxyClient runread Excp: "+io);
                            Byte abyte=channelByteHashMap.get(client);
                            list.add(abyte);
                        }
                        if (i<0){
//                            key.channel();
                            Byte abyte=channelByteHashMap.get(client);
                            list.add(abyte);
                        }
                    }else if (!key.isValid()){

                    }
                    iterator.remove();  //事件处理完毕，要记得清除
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    byte[] cl=new byte[1];
    public synchronized void  removeChannel(Byte b) throws IOException {
        SocketChannel channel=socketMap.get(b);
        System.out.println("ProxyClient  remove :  "+b);
        if (channel!=null){
            channelByteHashMap.remove(channel);
            socketMap.remove(b);
            channel.close();
            cl[0]= (byte) (b.byteValue()|0b10000000);
            streamAB.send(cl);
        }
    }

    public void removeChannel(SocketChannel channel) throws IOException {
        if (!channel.isConnected()){
            channel.close();
            Byte abyte=channelByteHashMap.get(channel);
            channelByteHashMap.remove(channel);
            socketMap.remove(abyte);
            cl[0]= (byte) (abyte.byteValue()|0b10000000);
            streamAB.send(cl);
        }
    }
}
