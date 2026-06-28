package superlink.udpbind.client.recives;

import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Arrays;
import java.util.Map;


public class MainReciverques implements Runnable,RunTime {
    public DatagramSocket datagramSocket;
    public Integer userid;
    public Short id;
    public boolean mode=false;
    public boolean run=true;
    public Thread thread;
    public MainReciverques(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }
    byte[] bytes=new byte[65507];
    byte[] zero=new byte[6];
    DatagramPacket packet = new DatagramPacket(bytes, 65507);

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public void initSocket(DatagramSocket datagramSocket) {
//        DatagramSocket old=this.datagramSocket;
        this.datagramSocket = datagramSocket;
    }

    //@Fork(value = 1, jvmArgs = "-XX:-StackTraceInThrowable")
    @Override
    public void run() {
        if(InitClass.ThreadMode==1){
            return;
        }
        System.out.println("start  MainReacterThread");
        if (thread==null){
            thread=Thread.currentThread();
        }else {
            Thread.currentThread().setName("priorityThread");
            return;
        }
        thread.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof StackOverflowError) {
                System.err.println("线程因栈溢出终止！");
            } else {
                System.err.println("线程因其他异常终止: " + e.getClass());
            }
            thread=null;
            System.out.println(Thread.currentThread());
        });
        Thread.currentThread().setName("MainReacterThread");
        Thread.currentThread().setPriority(8);
        Map<Short, ByteBufer> map=null;
        UserContext userContext = null;
        byte[] bytes=this.bytes;
        byte[] zero=this.zero;
        ByteBufer bufer;
        DatagramPacket packet = this.packet;
        if (mode){
            while (run) {
                try {
                    datagramSocket.receive(packet);
                } catch (SocketTimeoutException e) {
                    UDPclient.initSocket();
                    System.out.println("超时，重置socket");
                    // 超时是正常的，继续循环，顺便检查 isRunning 状态
                }catch (SocketException e){
                    if ("Socket closed".equals(e.getMessage())) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    } else {
                        // 其他网络异常（如网络不可达）

                    }
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("主连接连接超时");
//                    run=false;
                    break;
                }
                try {
                    userid = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
                    id = (short)((bytes[4] & 0xFF) << 8 | (bytes[5] & 0xFF));
//                    if(id!=0&&id!=5&&id!=7){
//                        System.out.println(id);
//                    }
                    userContext = UDPclient.mainDataQueue.getQueUser(userid);
                    bufer=userContext.map.get(id);
                    bufer.add(packet);
                    System.arraycopy(zero, 0, bytes, 0, 6);
                    if (id == 0) {
                        BindFactory.checkthread.interrupt();
                    }
                }catch (IllegalArgumentException e){
                    System.out.println("捕获IllegalArgumentException userid: "+ userid+"   id: "+id);
                    System.out.println(e.getMessage());
                    System.out.println(Arrays.toString(Utils.subByte(bytes,0,packet.getLength())));
                } catch (IllegalStateException e){
                    e.printStackTrace();
                    System.out.println("捕获IllegalStateException userid: "+ userid+"   id: "+id);
                }catch (NullPointerException e){
                    System.out.println("null userid: "+ userid+"   id: "+id);
                    if (userContext==null){
                        if (packet.getLength()<40){
                            System.out.println(new String(bytes,0, packet.getLength()));
                            System.out.println(Arrays.toString(Utils.subByte(bytes,0,20)));
                        }else {
                            try {
                                System.out.println(new String(bytes,0,40,"utf-8")+"      byte length:  "+packet.getLength());
                                System.out.println(Arrays.toString(Arrays.copyOfRange(bytes,0,40)));
                            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                                unsupportedEncodingException.printStackTrace();
                            }
                        }
                    }
                    e.printStackTrace();
                }catch (Exception e){
                    System.out.println("捕获错误userid: "+ userid+"   id: "+id);
                    System.out.println(Arrays.toString(Utils.subByte(bytes,0,packet.getLength())));
                    e.printStackTrace();
                }
            }
        }else {
            while (run) {
                try {
                    datagramSocket.receive(packet);
                } catch (IOException e) {
                    System.out.println("主连接连接超时");
                    break;
                }
            try {
                //System.out.println("RECV: " +packet.getAddress().toString()+":"+ packet.getPort());
                userid =(bytes[0]& 0xFF)<<24 | (bytes[1]& 0xFF)<<16 | (bytes[2]& 0xFF)<<8 | (bytes[3]& 0xFF);
                id = (short)((bytes[4] & 0xFF) << 8 | (bytes[5] & 0xFF));
                userContext = UDPclient.mainDataQueue.getQueUser(userid);
                bufer=userContext.map.get(id);
                bufer.add(packet);
                System.arraycopy(zero,0,bytes,0,6);
            } catch (Exception e) {
                System.out.println("捕获错误userid: "+ userid+"   id: "+id);
                e.printStackTrace();
                if (bytes!=null){
                    if (bytes.length<20){
                        System.out.println(new String(bytes));
                        System.out.println(Arrays.toString(bytes));
                    }else {
                        try {
                            System.out.println(new String(bytes,0,40,"utf-8")+"      byte length:  "+packet.getLength());
                            System.out.println(Arrays.toString(Arrays.copyOfRange(bytes,0,40)));
                        } catch (UnsupportedEncodingException unsupportedEncodingException) {
                            unsupportedEncodingException.printStackTrace();
                        }
                    }
                }
            }
        }
        }
        Thread.currentThread().setName("priorityThread");
        thread=null;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public void setTime(long time) {

    }

    @Override
    public int getTimes() {
        return 0;
    }

    @Override
    public void setTimes(int times) {

    }

    @Override
    public void decTimes() {

    }

    @Override
    public void process() {
        if (InitClass.ThreadMode!=1){
            return;
        }
        UserContext userContext = null;
        ByteBufer bufer;
        CompatibleDatagramSocket socket= (CompatibleDatagramSocket) datagramSocket;
        try {
            ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
            InetSocketAddress socketAddress = socket.receive(byteBuffer);
            if (socketAddress==null)return;
            packet.setPort(socketAddress.getPort());
            packet.setAddress(socketAddress.getAddress());
            packet.setLength(byteBuffer.position() + packet.getOffset());
            userid = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
            id = (short)((bytes[4] & 0xFF) << 8 | (bytes[5] & 0xFF));
            userContext = UDPclient.mainDataQueue.getQueUser(userid);
            bufer=userContext.map.get(id);
            bufer.add(packet);
            System.arraycopy(zero, 0, bytes, 0, 6);
//            if (id == 0) {
//                BindFactory.checkthread.interrupt();
//            }
//        } catch (IOException e) {
//            System.out.println("主连接连接超时");
        }catch (IllegalArgumentException e){
            System.out.println("捕获IllegalArgumentException userid: "+ userid+"   id: "+id);
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(Utils.subByte(bytes,0,packet.getLength())));
        } catch (IllegalStateException e){
            e.printStackTrace();
            System.out.println("捕获IllegalStateException userid: "+ userid+"   id: "+id);
        }catch (NullPointerException e){
            System.out.println("null userid: "+ userid+"   id: "+id);
            if (userContext==null){
                if (packet.getLength()<40){
                    System.out.println(new String(bytes,0, packet.getLength()));
                    System.out.println(Arrays.toString(Utils.subByte(bytes,0,20)));
                }else {
                    try {
                        System.out.println(new String(bytes,0,40,"utf-8")+"      byte length:  "+packet.getLength());
                        System.out.println(Arrays.toString(Arrays.copyOfRange(bytes,0,40)));
                    } catch (UnsupportedEncodingException unsupportedEncodingException) {
                        unsupportedEncodingException.printStackTrace();
                    }
                }
            }
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("捕获错误userid: "+ userid+"   id: "+id);
            System.out.println(Arrays.toString(Utils.subByte(bytes,0,packet.getLength())));
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 启动接收线程（非阻塞模式）
        new Thread(() -> {
            try {
                testNonBlockingReceive();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 主线程发送数据
        try {
            Thread.sleep(1000); // 确保接收线程先启动
            sendTestData();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试非阻塞模式下是否能通过 socket().receive(packet) 接收数据
     */
    private static void testNonBlockingReceive() throws IOException, InterruptedException {
        DatagramChannel channel = DatagramChannel.open();
//        channel.configureBlocking(false); // 设为非阻塞
        channel.bind(new InetSocketAddress(9000));

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        System.out.println("[Receiver] 非阻塞模式，尝试调用 socket().receive(packet)...");
        try {
            channel.socket().receive(packet); // 预期抛出 IllegalBlockingModeException
            System.out.println("[Receiver] 意外：方法调用成功（应该抛出异常）");
        } catch (IllegalBlockingModeException e) {
            System.out.println("[Receiver] 预期异常：IllegalBlockingModeException");
            System.out.println("[Receiver] 原因：socket().receive(packet) 仅支持阻塞模式");
        }

        // 正确做法：使用 channel.receive(ByteBuffer)
        System.out.println("\n[Receiver] 改用 channel.receive(ByteBuffer)...");
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        SocketAddress sender = channel.receive(byteBuffer); // 非阻塞，可能返回 null
        if (sender != null) {
            byteBuffer.flip();
            byte[] receivedData = new byte[byteBuffer.limit()];
            byteBuffer.get(receivedData);
            System.out.println("[Receiver] 收到数据：" + new String(receivedData) + " 来自 " + sender);
        } else {
            System.out.println("[Receiver] 无数据可读（非阻塞模式正常行为）");
        }

        channel.close();
    }

    /**
     * 发送测试数据
     */
    private static void sendTestData() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String message = "Hello, Non-Blocking UDP!";
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length,
                new InetSocketAddress("localhost", 9000)
        );
        socket.send(packet);
        System.out.println("[Sender] 已发送数据：" + message);
        socket.close();
    }
}


