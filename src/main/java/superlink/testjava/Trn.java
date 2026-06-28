package superlink.testjava;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Trn implements Callable,Runnable {
    public int a;
    public int b;
    public DatagramSocket socket;
    public Trn(int aa, DatagramSocket socket){

        this.socket=socket;

        a=aa;
        b=aa;

    }
    @Override
    public Integer call(){
        System.out.println("b:"+b);
        return b;
    }


    @Override
    public void run() {
        while (true){
            System.out.println(a);
            b=b+1;
            DatagramPacket packet=new DatagramPacket(new byte[10],10);
            try {
                System.out.println("a:"+a+"-readlyrev");
                socket.receive(packet);
                System.out.println("a:"+a+"-revafter");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("ID"+a+":"+new String(packet.getData()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(a);
            System.out.println(a+"b:"+b);
        }

    }


}
