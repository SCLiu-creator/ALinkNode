package superlink.udpbind.client.recives.data.stream;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

//不使用
public class DataStream<E> implements ByteBufer {
    //byte[] =1450
    public static Map<String,DataStream> streamMap=new HashMap<>();
    public  AtomicBoolean atomicBoolean;
    public StreamReador streamReador;
    public StreamSendor streamSendor;
    public UserContext userContext;
    public short id;
    public Senders senders;
    public ByteBufer blockingQueue;
    public superlink.udpbind.usedata.DataRequest sdr;
    public byte[] rev;

//    public DataStream(String username){
//        System.out.println("revor");
//        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
//        this.id = userContext.newQueue();
//        blockingQueue = userContext.getDataQue(this.id);
//        this.senders = new Senders().Init(this.id, username);
//        this.streamReador=new StreamReador(this);
//        streamMap.put(username,this);
//    }
//    public DataStream(String username, int id) {
//        System.out.println("sendor");
//        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
//        this.id = (short)id;
//        blockingQueue = userContext.getDataQue(this.id);
//        this.senders = new Senders().Init(this.id, username);
//        this.streamSendor=new StreamSendor(this);
//        streamMap.put(username,this);
//    }
    public void createDS(){
        DataRequest dataRequest=new DataRequest();
        dataRequest.id=this.id;
        String send="DS";
        byte[] bytes=send.getBytes();
        senders.sendSym(bytes);
        senders.sendSym(bytes);
        this.atomicBoolean=new AtomicBoolean(false);
    }


    byte aByte=0;
    public void add(DatagramPacket packet) {
        throw new IllegalStateException("UnImplement");
    }

    @Override
    public boolean add(byte[] o) {
        byte b=(o)[0];
        if ((b & 0b00000001) != 0) {//sender|reader
            if ((b & 0b00000010) != 0) {//a|b
                if ((b & 0b00000100) != 0) {
                    streamSendor.list1[((byte[])o)[1]+128]=(byte[]) o;
                } else {
                    senders.send(streamSendor.list1[((byte[])o)[1]+128]);
                }
            } else {
                if ((b & 0b00000100) != 0) {

                    streamSendor.list2[((byte[])o)[1]+128]=(byte[]) o;
                } else {
                    senders.send(streamSendor.list2[((byte[])o)[1]+128]);
                }
            }
        } else {
            if ((b & 0b00000010) != 0) {//a|b
                if ((b & 0b00000100) != 0) {

                    streamReador.list1[((byte[])o)[1]+128]=(byte[]) o;
                } else {
                    senders.send(streamReador.list1[((byte[])o)[1]+128]);
                }
            } else {
                if ((b & 0b00000100) != 0) {

                    streamReador.list2[((byte[])o)[1]+128]=(byte[]) o;
                } else {
                    senders.send(streamReador.list2[((byte[])o)[1]+128]);
                }
            }
        }




        if (((byte[])o)[0]==127){
            streamReador.list1[((byte[])o)[1]+128]=(byte[])o;
        }else if(((byte[])o)[0]==-128) {
            streamReador.list2[((byte[])o)[1]+128]=(byte[])o;
        }else if(((byte[])o)[0]==0) {
            close();
        }





        if (((byte[])o)[0]==127){
            streamReador.list1[((byte[])o)[1]+128]=(byte[])o;
        }else if(((byte[])o)[0]==-128) {
            streamReador.list2[((byte[])o)[1]+128]=(byte[])o;
        }else if(((byte[])o)[0]==0) {
            close();
        }
        return false;
    }

    public boolean close(){
        userContext.deltask(id);
        streamMap.remove(userContext.userName);
        return false;
    }

    @Override
    public byte[] poll() {
        return null;
    }


    @Override
    public byte[]take() throws InterruptedException {
        return null;
    }

    @Override
    public byte[]poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }



    @Override
    public void clear() {

    }



    @Override
    public int size() {
        return 0;
    }

}
