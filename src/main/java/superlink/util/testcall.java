package superlink.util;

import superlink.udpbind.usedata.UserRequest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.*;

public class testcall implements Recivetor ,Runnable{
    public static DatagramSocket resocket;
    public testcall(DatagramSocket socket){
        resocket=socket;

    }



    public <V> UserRequest Recive1(V o) {

        UserRequest userRequest= new UserRequest();

        return userRequest;
    }


    @Override
    public UserRequest Recive(Object o) {
        return null;
    }
    public byte[] ReciveData()throws InterruptedException,ExecutionException{
        ExecutorService service= Executors.newFixedThreadPool(6);
        Future future=service.submit(new Recivetor());

        DatagramPacket re=(DatagramPacket) future.get();

        return re.getData();




    }

    @Override
    public void run() {

    }


    class Recivetor implements Callable<DatagramPacket> {

        @Override
        public DatagramPacket call() throws Exception {
            DatagramPacket packet=new DatagramPacket(new byte[1472],1472);
            while (true){
                resocket.receive(packet);
                return packet;
            }


        }
    }
}
