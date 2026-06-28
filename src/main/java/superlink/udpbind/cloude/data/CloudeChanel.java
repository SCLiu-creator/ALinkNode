package superlink.udpbind.cloude.data;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.cloude.FileTrigger;
import superlink.udpbind.usedata.DataRequest;

import java.net.DatagramPacket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CloudeChanel {
    private BufferQue sendQueue;
    private BufferQue reciveQueue;
    int oid;
    int did;
    Senders senders;
    public ChanlsFactory.ID idkey;
    public List<Object> orderlist=new ArrayList<>();
    public ID build(UserContext userContext){
        //todo
        this.sendQueue =new BufferQue(256);
        this.reciveQueue =new BufferQue(256);
        this.oid=userContext.newQueue(sendQueue);
        this.did=userContext.newQueue(reciveQueue);
        ID id=new ID();
        id.name= UDPclient.userlocal.username;
        id.oid=this.oid;
        id.did=this.did;
        senders=new Senders().Init((short)did,userContext.userName);
        return id;
    }
    public void build(UserContext userContext,ChanlsFactory.ID id){
        this.idkey=id;
        this.sendQueue = new BufferQue(256);
        userContext.setQueue((short) id.you,sendQueue);
        this.reciveQueue = new BufferQue(256);
        userContext.setQueue((short) id.you1,reciveQueue);;
        this.oid=id.you;
        this.did=id.you1;
        senders=new Senders().Init((short)did,userContext.userName);
    }



    private byte[] bytesbuffer;
    private int readPos;
    public int read(byte[] bytes){
        if (bytesbuffer==null){
            bytesbuffer=reciveQueue.arrayList[reciveQueue.rpos];
            while (bytesbuffer==null){
                senders.sendsyn(posToByteArray(reciveQueue.rpos),byteChar[3]);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            readPos=0;
        }
        if (bytesbuffer.length-readPos<bytes.length){
            System.arraycopy(bytesbuffer,readPos,bytes,0,bytesbuffer.length-readPos);
            bytesbuffer=null;
            return bytesbuffer.length-readPos;
        }else {
            System.arraycopy(bytesbuffer,readPos,bytes,0,bytes.length);
            readPos=readPos+bytes.length;
            return bytes.length;
        }
    }

    public byte[][] byteChar=new byte[][]{
            new byte[]{1},//常规发送
            new byte[]{2},//重传
            new byte[]{3},//请求重传
            new byte[]{4},
    };
    public boolean write(byte[] bytes){
        byte[] bytesb;
        while ((bytesb=sendQueue.buffer.peekFirst())!=null){
            try {
                if (bytesb[2]==3){
                    int i=byteArrayToPos(bytesb);
                    byte[] bytes1=posToByteArray((short) i);
                    senders.sendsyn(bytes1,byteChar[1],sendQueue.arrayList[i]);
                }
            }catch (Exception e){

            }
        }
        sendQueue.wpos++;
        if (sendQueue.wpos>65505){
            sendQueue.wpos=0;
        }
        sendQueue.arrayList[sendQueue.wpos]=bytes;
        return true;
    }

    public static class BufferQue implements ByteBufer {
        public byte[][] arrayList;
        public ArrayDeque<byte[]> buffer;
        AtomicInteger atomicInteger=new AtomicInteger(0);
        public int cap;
        public static int def;
        public short wpos=0;
        public volatile short rpos=0;
        public boolean ab=false;

        public int bufferR=0;
        public int bufferL=0;
        /**
         * 占据3字节
         * **/
        public BufferQue(int size){
//            arrayList=new NodeBuffer(256*256);
            buffer=new ArrayDeque(256);
            bufferL=256;
        }


        @Override
        public void add(DatagramPacket packet) {
            throw new IllegalStateException("UnImplement");
        }

        @Override
        public boolean add(byte[] bytes) {
            if (bytes[2]==0){
                int i= byteArrayToPos(bytes);

                if (i>wpos&& i>rpos && i<65505){
                    arrayList[i]=bytes;
                    cap=cap+(i-wpos);
                    wpos= (short) i;
                }
                if (i<wpos&& i<rpos && i<65505){
                    arrayList[i]=bytes;
                    cap=cap+(65506-wpos)+i;
                    wpos= (short) i;
                }
                if (i>wpos&& i<rpos){
                    arrayList[i]=bytes;
                    cap=cap+(i-wpos);
                    wpos= (short) i;
                }


//                if (i>wpos && i>rpos){
//
//                }
//                if (i>wpos && i<rpos){
//                    arrayList.value[i]=bytes;
//
//                }
//                if (i<wpos && i<rpos){
//                    arrayList.value[i]=bytes;
//                }
//                if (i<wpos && i>rpos){
//                    arrayList.value[i]=bytes;
//                }
                if (i>cap && i<65535){
                    cap=i;
                }
            }else {
                if (bytes[2]==2){
                    int i= byteArrayToPos(bytes);
                    if (wpos>rpos && i<wpos && i>rpos){
                        arrayList[i]=bytes;
//                        buffer.
                    }
                    if (wpos<rpos ){
                        if (i>rpos){
                            arrayList[i]=bytes;
                        }
                        if (i<wpos){
                            arrayList[i]=bytes;
                        }
                    }

                }
                if (bytes[2]==3){
                    buffer.add(bytes);

                }
                //todo
                //over
            }
            return true;
        }

        public boolean offer() {
            if (bufferL<128){
//                if (bufferR+bufferL){
//                    buffer.set()
//                }
//                buffer.add(arrayList.value[rpos]);
                bufferL++;
//                rpos++;
            }
            return false;
        }

        @Override
        public byte[] poll() {
            return  null;
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


    public boolean transmit(FileTrigger.TargetFile targetFile){
      String s=JSON.toJSONString(targetFile);
      senders.send(s.getBytes());
      return true;
    }

    public byte[] transmit(){
        byte[] order= sendQueue.poll();
        if (order!=null){
            DataRequest dataRequest= JSON.parseObject(order,DataRequest.class);
            int l=dataRequest.page;
            List<byte[]> list=new LinkedList<>();
            while (l>0){
                byte[] bytes = null;
                try {
                    bytes= reciveQueue.poll(300, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bytes==null){
                    continue;
                }
                list.add(bytes);
                senders.send("OK".getBytes());
            }
            return order;
        }else {
            senders.send("OK".getBytes());
            return order;
        }
    }

    public void transmit(byte[] bytes){
        int length=bytes.length;
        int page=length/1024;
        int remainder=length%1024;
        if (remainder!=0){
            byte[] send=new byte[1024];
            int i=0;
            while (i<page){
                System.arraycopy(bytes,i*1024,send,0,1024);
                senders.send(bytes);
            }
            send=new byte[1024];
            System.arraycopy(bytes,i*1024,send,0,remainder);
            senders.send(bytes);

        }else {
            byte[] send=new byte[1024];
            int i=0;
            while (i<page){
                System.arraycopy(bytes,i*1024,send,0,1024);
                senders.send(bytes);
            }
        }

    }

    public static int byteArrayToPos(byte[] bytes) {
        int value ;
        value= ((bytes[0]& 0xFF)<<8);
        value= (value+(bytes[1]& 0xFF));
        value=value+256*256;
        return value;
    }
    public static byte[] posToByteArray(int i) {
        byte[] result = new byte[2];
        i= (short) (i-65506);
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

}
