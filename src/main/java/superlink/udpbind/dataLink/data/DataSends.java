package superlink.udpbind.dataLink.data;

import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;

import static superlink.util.Utils.byteMerger;
import static superlink.util.Utils.subByte;

//todo
public class DataSends implements Runnable{

    public DataRequest dataRequest;
    public UdpData udpData;
    public int id;
    public DataQueue dataQueue;
    public byte[] byteid;
    public int bufferlen=1024;

    public DataSends(DataRequest dataRequest){
        this.dataRequest=dataRequest;
        this.udpData= Handler.UdpMap.get(dataRequest.requestname);
        this.id=dataRequest.id;
        this.byteid=new byte[]{(byte) this.id};
        this.dataQueue= ReciveQueueFactory.getDataQueue(udpData.userRequest.username);

        System.out.println("DataSends:"+id);
    }


    @Override
    public void run() {
        byte[] bytes;
        while (true){
            try {
                bytes=dataQueue.getdata(id, (long) 30);
                Optional<byte[]> optionalBytes=Optional.ofNullable(bytes);
                if (!optionalBytes.isPresent()){//ArrayUtils
                    System.out.println("null超时");
                    dataQueue.deltask(byteid[0]);
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
                    MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
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

                        String prex=String.valueOf(index+1000000000);
                        byte[] bsend=byteMerger(byteid,prex.getBytes());
                        bsend=byteMerger(bsend,buffers);
                        DatagramPacket datagramPacket=new DatagramPacket(bsend,bsend.length,this.udpData.userRequest.toaddress,this.udpData.userRequest.toport);
                        try {
                            Thread.sleep(10,20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.udpData.dataSocket.send(datagramPacket);
                        index=index+1;
                    }

                    byte[] cheak;
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(new byte[1472], 1472);
                        try {
                            cheak=dataQueue.getdata(id, (long) 12);
                            if (cheak==null){
                                randomAccessFile.close();
                                dataQueue.deltask(byteid[0]);
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
                            dataQueue.deltask(byteid[0]);
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
                        String prex=String.valueOf(index+1000000000);
                        byte[] bsend=byteMerger(byteid,prex.getBytes());
                        bsend=byteMerger(bsend,buffers);
                        DatagramPacket datagramPacket = new DatagramPacket(bsend, bsend.length, udpData.userRequest.toaddress, udpData.userRequest.toport);
                        this.udpData.dataSocket.send(datagramPacket);
                    }
                    System.gc();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//no LR

        }
    }


}
