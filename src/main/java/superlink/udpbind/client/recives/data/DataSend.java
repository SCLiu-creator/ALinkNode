package superlink.udpbind.client.recives.data;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static superlink.util.Utils.byteMerger;
import static superlink.util.Utils.subByte;


public class DataSend implements Runnable{

    public DataRequest dataRequest;
    public short id;
    public UserContext userContext;
    public ByteBufer blockingQueue;
    public int bufferlen=1024;

    public DataSend(DataRequest dataRequest) throws Exception {
        this.dataRequest=dataRequest;
        this.id=(short)dataRequest.id;
        this.userContext= UDPclient.mainDataQueue.getUserContext(dataRequest.requestname);
        this.blockingQueue=userContext.getQueue(id);

        System.out.println("userContext:"+id);
    }


    @Override
    public void run() {
        byte[] bytes;
        while (true){
            try {
                bytes=blockingQueue.poll(30, TimeUnit.SECONDS);
                Optional<byte[]> optionalBytes=Optional.ofNullable(bytes);
                if (!optionalBytes.isPresent()){//ArrayUtils
                    System.out.println("null超时");
                    userContext.deltask(id);
                    break;}
            } catch (InterruptedException | NullPointerException e) {
                System.out.println("超时");
                e.printStackTrace();
                break;
            }
            System.out.println(new String(bytes));
            String star=new String(bytes,0,4);

            if ("star".equals(star)){
                try {

                    File file=new File(this.dataRequest.dir);
                    FileInputStream fileInputStream=new FileInputStream(file);
                    FileChannel fileChannel=fileInputStream.getChannel();
                   // MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
                    RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");

                    byte[] buffer=new byte[bufferlen];//65493
                    int index=0;
                    long filelong=fileChannel.size();
                    int page=0;
                    page=Math.toIntExact(filelong/bufferlen);//65493
                    boolean p=(filelong%bufferlen) != 0;//65493
                    if (p){
                        page=page+1;
                    }

                    byte[] buffers;
                    while (page>index){

                        try {
                            int len=randomAccessFile.read(buffer);
                            buffers=subByte(buffer,0,len);
                            //    mappedByteBuffer.get(buffer);
                        }catch (Exception e){
                            System.out.println("out mappbytebuffer index");
                            break;
                        }

                        String prex=  String.valueOf(index+1000000000);
                        byte[] bsend=byteMerger(prex.getBytes(),buffers);
                        bsend=byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) id),bsend);
                        Senders.Sends(this.userContext.inetAddress,this.userContext.port,bsend);
                        index=index+1;
                    }

                    byte[] cheak;
                    while (true) {

                        try {
                            cheak=blockingQueue.poll( 12,TimeUnit.SECONDS);
                            if (cheak==null){
                                randomAccessFile.close();
                                userContext.deltask(id);
                                break;}
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }

//                        if ("OK".equals(po.replace("\u0000",""))) {
//                            randomAccessFile.close();
//                            break;
//                        } else {
//                            new Thread(new ChooseDeal(packet)).start();
//                        }
                        if (cheak[0]==79 && cheak[1]==75) {
                            randomAccessFile.close();
                            userContext.deltask(id);
                            break;
                        }

                        String po = new String(cheak, 0, 10);
                        Integer integer =Integer.valueOf(po);
                        integer=integer-1000000000;
                        randomAccessFile.seek(integer*bufferlen);
                        try {
                            int len=randomAccessFile.read(buffer);
                            buffers=subByte(buffer,0,len);

                            //    mappedByteBuffer.get(buffer);
                        }catch (Exception e){
                            System.out.println("out mappbytebuffer index");
                            break;
                        }
                        //mappedByteBuffer.get(bytes);
                        String prex=String.valueOf(integer+1000000000);
                        byte[] bsend=byteMerger(prex.getBytes(),buffers);
                        bsend=byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) id),bsend);
                        Senders.Sends(this.userContext.inetAddress,this.userContext.port,bsend);
                    }
                    System.gc();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//no LR

        }
    }

    public String sprex(){
        if (id<10){
            return "000"+id;
        }else if (id<100){
            return "00"+id;
        }else if (id<1000){
            return "0"+id;
        }else if (id<8999){
            return ""+id;
        }else {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
