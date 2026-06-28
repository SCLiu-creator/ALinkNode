package superlink.udpbind.client.recives.data.stream;

import superlink.udpbind.client.recives.Senders;

import java.util.concurrent.TimeUnit;

public class StreamSendor {
    public Senders senders;
    public buffer buffer;
    public DataStream dataStream;
    public StreamSendor (DataStream dataStream){
        this.senders=dataStream.senders;
        this.dataStream=dataStream;
        this.buffer=new buffer(this.list1,this.list2);
    }


    public int pos=0;
    public byte[][] list1=new  byte[256][];
    public byte[][] list2=new  byte[256][];

    public int r=0;
    public boolean aBool=true;
    public boolean sonBool=true;
    public byte[] tran;

    //1,重传 0，结束本次读写， -1，切下一个缓冲区
    public void write(byte[] bytes) throws Exception {
        r=0;
        while (aBool){

            byte[] bytes1=new byte[0];
            byte[][] list=buffer.getlist();
            do {
                bytes1=subByte02(bytes,r*1448,1448);
                if (bytes1.length<1450){
                    bytes1[0]=buffer.aByte;
                    bytes1[1]= (byte) (r%256+128);
                }else {
                    bytes1[0]=-1;
                    bytes1[1]= (byte) (r%256+128);
                }

                list[r]=bytes1;
                senders.send(bytes1);
                r++;
            }while (bytes1.length==1450 && r%256<255);
            sonBool=true;
            if (r%256>=255){
                while (sonBool){
                    tran= (byte[]) dataStream.blockingQueue.poll(6000, TimeUnit.SECONDS);
                    if (tran[0]==0){
                        buffer.clear();
                        return;
                    }  else if (tran[0]==1){
                        senders.send(list[tran[1]+128]);
                    }else if (tran[0]==-1){
                        buffer.clear();
                        sonBool=false;
                        r++;
                    }else if(tran[0]!=buffer.aByte){
                        buffer.clear();
                        sonBool=false;
                        r++;
                    }
                }

            }else {
                while (sonBool){
                    tran= (byte[]) dataStream.blockingQueue.poll(6000, TimeUnit.SECONDS);
                    if (tran[0]==0){
                        buffer.clear();
                        return;
                    }  else if (tran[0]==1){
                        senders.send(list[tran[1]+128]);
                    }else if (tran[0]==-1){
                        sonBool=false;
                        buffer.clear();
                        return;
                    }else {
                        buffer.clear();
                        return;
                    }
                }
            }
        }
    }
    boolean ab;
    public void write0(byte[] bytes) throws Exception {
        if (ab){

        }else {

        }

    }

    class buffer{
        public byte[][] list1=new  byte[256][];
        public byte[][] list2=new  byte[256][];
        public byte aByte=127;
        boolean state=true;
        public buffer(byte[][] list1,byte[][] list2){
            this.list1=list1;
            this.list2=list2;
        }
        public byte[][] getlist(){

            if (state){
                state=!state;
                aByte=127;
                return list1;
            }else {
                state=!state;
                aByte=-128;
                return list2;
            }
        }
        int sn=0;
        public void clear(){

            if (!state){
                for (;sn!=255;sn++){
                    list1[sn]=null;
                }
            }else {
                for (;sn!=255;sn++){
                    list2[sn]=null;
                }
            }
            sn=0;
        }
    }

    public static class reSendor{
        public reSendor(){

        }


    }

    public static byte[] subByte02(byte[] b, int off, int length) {
        byte[] b1 = new byte[length+2];
        if (b.length < (off + length)) {
            b1 = new byte[b.length - off];
            System.arraycopy(b, off, b1, 2, b.length - off);
        } else {
            System.arraycopy(b, off, b1, 2, length);
        }

        return b1;
    }

}
