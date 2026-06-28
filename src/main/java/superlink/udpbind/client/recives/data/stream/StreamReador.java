package superlink.udpbind.client.recives.data.stream;

import superlink.udpbind.client.recives.Senders;

public class StreamReador {
    public Senders senders;
    public StreamReador.buffer buffer;
    public DataStream dataStream;
    public StreamReador (DataStream dataStream){
        this.senders=dataStream.senders;
        this.dataStream=dataStream;
        this.buffer=new StreamReador.buffer(this.list1,this.list2);
    }
    public int pos=0;
    public byte[][] list1=new  byte[256][];
    public byte[][] list2=new  byte[256][];

    public int r=0;
    public boolean aBool=true;
    public boolean sonBool=true;
    public byte[] tran=new byte[2];

    public int readnull=0;
    public void read(byte[] bytes) throws InterruptedException {
        r=0;
        while (aBool){
            int tm=0;
            byte[] bytes1=new byte[0];
            byte[][] list=buffer.getlist();
            readnull=0;
            do {
                bytes1=list[r%256];
                if (bytes1==null){
                    if (readnull < r){
                        tran[0]=buffer.aByte;
                        tran[1]= (byte) (r%256-128);
                        senders.send(tran);
                        r++;
                        readnull=r;
                    }else {
                        r=r-r%256;
                    }

                }else if (bytes1[0]==-1){
                    bytes1[0]=1;
                    System.arraycopy(bytes1, 2, bytes, r*1448, bytes1.length - 2);
                    tm++;
                    r++;
                }else if (bytes1[0]==buffer.aByte){
                    if (tm==r%256-1){
                        tran[0]=-1;
                        tran[1]= (byte) (r%256-128);
                        System.arraycopy(bytes1, 2, bytes, r*1448, bytes1.length - 2);
                        senders.send(tran);
                    }else {
                        tm=0;
                        r=r-r%256;
                    }
                }
            }while (bytes1.length==1450 && r%256<255);
           r++;

        }
    }


    public byte[] read0() throws InterruptedException {
        r=0;
        return buffer.getlist()[r];
    }
    int time=1;
    byte[] sy=new byte[]{0b0000010,0};
    public byte[] read1() throws InterruptedException {
        r=0;
        synchronized (this){
            byte[] bytes=null;
            while (true){
                bytes=buffer.getlist()[r];
                if (bytes==null){
                    Thread.sleep(time*500);
                    senders.send(new byte[]{0b00000110, (byte) (r-128)});
                    time++;
                }else {
                    time=1;
                    break;
                }
            }


            return bytes;
        }

    }
    class buffer{
        public byte[][] list1=new  byte[256][];
        public byte[][] list2=new  byte[256][];
        public byte[] sy1=new  byte[]{0b00000011};
        public byte[] sy2=new  byte[]{0b00000001};
        public byte[] sy11=new  byte[]{0b00000111};
        public byte[] sy22=new  byte[]{0b00000001};
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
//        public byte[]getsy(){
//            if (state){
//                state=!state;
//                aByte=127;
//                return list1;
//            }else {
//                state=!state;
//                aByte=-128;
//                return list2;
//            }
//        }
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
}
