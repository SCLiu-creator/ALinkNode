package superlink.testjava;

import superlink.util.Utils;

import java.io.IOException;

public class selecto {
    public static void main(String[] args) throws IOException {
        int i=12313123;
        byte b=(byte)i;
        byte[] bytes= Utils.intToByteArray(i);
        byte[] bytes1=(""+i).getBytes();
        Thread t1=new Thread(()->{
            int ii=100000000;
            long l=System.currentTimeMillis();
            while (ii>0){
                //Integer integer=Utils.byteArrayToInt(bytes);
                int bb=b;
                Integer bbb=bb;
                ii--;
            }
            l=System.currentTimeMillis()-l;
            System.out.println("over:t1:"+l);
        });
        Thread t2=new Thread(()->{
            int ii=100000000;
            long l=System.currentTimeMillis();
            while (ii>0){
                //Integer integer=Integer.valueOf(String.valueOf(bytes1));
                Byte bb=b;
                ii--;
            }
            l=System.currentTimeMillis()-l;
            System.out.println("over:t2:"+l);
        });
        t2.start();
        t1.start();
        i=1000;
        while (i>1){
            new Thread(()->{
                while (true){
                    System.out.println(1);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            i--;
        }
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Selector selector=Selector.open();
    }
}
