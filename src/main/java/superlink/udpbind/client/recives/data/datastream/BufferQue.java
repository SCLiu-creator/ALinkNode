package superlink.udpbind.client.recives.data.datastream;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.cloude.data.ChanlsFactory;
import superlink.udpbind.cloude.data.ID;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//弃用
public class BufferQue {

    private ByteBufer sendQueue;
    private ByteBufer reciveQueue;
    short oid;
    short did;
    Senders senders;
    public ChanlsFactory.ID idkey;
    public List<Object> orderlist=new ArrayList<>();

    public ID build(UserContext userContext){
        this.sendQueue =new RingQue(256);
        this.reciveQueue =new RingQue(256);
        this.oid=userContext.newQueue(sendQueue);
        this.did=userContext.newQueue(reciveQueue);
        ID id=new ID();
        id.name= UDPclient.userlocal.username;
        id.oid=this.oid;
        id.did=this.did;
        senders=new Senders().Init(did,userContext.userName);
        return id;
    }
    public void build(UserContext userContext,ChanlsFactory.ID id){
        this.idkey=id;
        this.sendQueue =userContext.getQueue((short) id.you);
        this.reciveQueue =userContext.getQueue((short)id.you1);;
        this.oid=(short)id.you;
        this.did=(short)id.you1;
        senders=new Senders().Init(did,userContext.userName);
    }

    public static class RingQue implements ByteBufer {
        NodeBuffer arrayListA;
        NodeBuffer arrayListB;
        AtomicInteger atomicInteger=new AtomicInteger(0);
        public int cap;
        public static int def;
        public int pos;
        public boolean ab=false;
        /**
         * b00000000
         * 1,控制符
         * 5-8，ab控制符
         * **/
        public RingQue(int size){
            arrayListA=new NodeBuffer(size);
            arrayListB=new NodeBuffer(size);
        }

        private NodeBuffer headn;


        private  NodeBuffer getWonde(byte b){
            if ((b & 0x0F) == 0x0F) {
                return arrayListA;
            } else if ((b & 0x0F) == 0x00) {
                // 检查后4位是否全为0
                return arrayListB;
            } else {
                System.out.println("后4位既不全为1也不全为0");
                return null;
            }
        }

        private NodeBuffer getRonde(){
            if(ab){
                return arrayListA;
            }else {
                return arrayListB;
            }
        }

        NodeBuffer wbuffer;
        @Override
        public boolean add(byte[] bytes) {
            NodeBuffer wbuffer=getWonde(bytes[0]);
            wbuffer.value[bytes[1]+128]=bytes;
            return true;
        }

        public void add(DatagramPacket packet) {
            throw new IllegalStateException("UnImplement");
        }


        @Override
        public byte[] poll() {
            NodeBuffer o=getRonde();
            if (o==null){
                return null;
            }

            return  o.getValue();
        }


        public boolean offer(byte[] o, long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public byte[] take() throws InterruptedException {
            return  null;
        }

        @Override
        public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public void clear() {
        }

        @Override
        public int size() {
            return atomicInteger.get();
        }

    }

    public static class NodeBuffer{

        NodeBuffer hand;
        byte[][] value;
        int size;
        int empty;
        int pos;
        int ros;
        public NodeBuffer (int size){
            value=new byte[size][];
        }
        public byte[] getValue() {
            return value[ros];
        }
        public void getValued() {
            ros--;
        }

        public void setValue(byte[] value) {
            int i=value[0]+128;
            this.value[i]=value;
            if (i>pos){
                pos=i;
            }
        }


        @Override
        public int hashCode(){
            return value.hashCode();
        }
        @Override
        public boolean equals(Object o){
            return value==o?true:false;
        }

    }
}
