package superlink.udpbind.client.recives.data.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class SteamTransfer {
    byte[] bytes=new byte[1000];
    byte[] buffsend=new byte[1000];
    public int post;
    public int mark;
    public int time;
    int bufferlong=256*256*2;
    byte[] buffrecive=new byte[10000];//512,1412,1024
    byte[] bufferA=new byte[bufferlong];//512,1412,1024
    byte[] bufferB=new byte[bufferlong];//512,1412,1024
    public AtomicReference<byte[]> atom=new AtomicReference<byte[]>();
    DatagramSocket socket;
    public buffer A;
    public buffer B;
    public byte[] buffer=new byte[0];
    public boolean bufferstate=true;
    boolean judge=true;

    public SteamTransfer(DatagramSocket socket,int time){
        this.socket=socket;
        this.time=time;
        this.A=new buffer((byte) 0);
        this.B=new buffer((byte) 1);
        Thread watch=new Thread(()->{
            w();
        });
    }

    public synchronized int read0(byte[] bytes){
        if (buffer.length>=bytes.length){
            System.arraycopy(bytes ,0,buffer,0,bytes.length);
            byte[] bytes1=new byte[buffer.length-buffer.length];
            System.arraycopy(buffer ,buffer.length,bytes1,0,buffer.length-buffer.length);
            buffer=bytes1;
        }else {
            System.arraycopy(bytes ,0,buffer,0,buffer.length);
            int l=bytes.length-buffer.length;
//            if (buffer.length>bytes.length){
//
//            }
        }
        return 0;
    }



    ReentrantLock lock=new ReentrantLock();
    public void w(){
        int i=0;
        while (true){
            byte[] bytes=atom.get();
            if (bytes==null){
                synchronized (lock){
                    try {
                        lock.wait(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    if (i>2){
                        int total=0;
                        for (int t=0;t<9;t++){
                            if (!A.l[t]){
                                se(t);
                            }else {
                                total++;
                            }
                            if (total==9){A.f=true;}
                        }
                        total=0;
                        for (int t=0;t<9;t++){
                            if (!B.l[t]){
                                se(t);
                            }else {
                                total++;
                            }
                            if (total==9){B.f=true;}
                        }
                    }

                }
            }else {
                if (bytes[0]==0){
                    A.write(bytes);
                    A.su();
                }else {
                    B.write(bytes);
                    B.su();
                }
                i=0;
                atom.set(null);

            }
            synchronized (lock){
                try {
                    lock.wait(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    public void close(){
        judge=false;
    }

    //ready
    public void write(byte[] data){
        synchronized (lock){
            atom.set(data);
            lock.notify();
        }
    }
    public void send(byte[] buffsend){

    }
    public void write(){

    }

    public void recive(){
        byte[] bytes=new byte[1024];
        re(bytes);
        System.arraycopy(bytes ,mark,buffrecive,post,bytes.length);
        post=post+bytes.length;
        //su();

    }
    public void recive(byte[] data){

        System.arraycopy(data ,mark,buffrecive,post,bytes.length);
        post=post+bytes.length;
       // su();

    }
    public byte[] read(byte[] bytes){
        if (A.f==true){
            buffrecive=A.b;
            System.arraycopy(buffrecive ,post,bytes,mark,bytes.length);

        }

        return bytes;
    }
    public byte read(int i){
        return buffrecive[i+post];
    }
    public void se(int p){
        //todo
        byte[] b=new byte[10];
        b[9]=(byte) p;
        try {
            DatagramPacket datagramPacket=new DatagramPacket(b,b.length, InetAddress.getLocalHost(),0000);
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void re(byte[] b){
        try {

            DatagramPacket datagramPacket=new DatagramPacket(b,b.length);
            socket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class buffer{
        public buffer(byte i){
            m=i;
        }
        byte m;
        int pos=0;
        int mark=0;
        boolean f=false;
        byte[] b=new byte[10240];
        boolean[] l=new boolean[10];
        public void write(byte[] bytes){
            l[bytes[1]]=true;
            System.arraycopy(bytes ,0,b,pos,bytes.length);
            pos=pos+bytes.length;
        }
        public void su(){
            try {
                DatagramPacket datagramPacket=new DatagramPacket("su".getBytes(),"su".getBytes().length);
                socket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
