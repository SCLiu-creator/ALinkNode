package superlink.testjava;

import org.checkerframework.checker.units.qual.C;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.baseMassage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class testBase64 {
    {
        BASE64Encoder encoder = new BASE64Encoder();
        String str = encoder.encode(new byte[10]).trim();
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] imgbyte = decoder.decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static class User implements baseMassage {
        public String nickName="";
        public String username;
        public InetAddress address;//公网ip
        public int port;
        public InetAddress inaddress;//本地ip
        public int inport;
        public boolean request=false;
        public int choose=0;
        public int udpstate;
//    public int tcpstate;
//    public int tcpstate2;

        @Override
        public boolean equals(Object o){
            boolean b=this.hashCode()==o.hashCode()?true:false;
            return b;
        }

        @Override
        public int hashCode(){
            return port&inport;
        }

    }

    public static void main(String[] args) {
        byte[] bytes0=new byte[]{'r','b'};
        String s=new String(bytes0);
        long ti=0;
        ti=System.currentTimeMillis();
        byte[] bytes=new byte[]{12,0};
        for (int i = 0; i < 1000*1000*1000; i++) {
//            if (bytes.length==1){
//                //进入第一分支执行更快
//            }else {
//
//            }
            if (bytes[1]==0){

            }
        }
        System.out.println(System.currentTimeMillis()-ti);

        ConcurrentHashMap concurrentHashMap=new ConcurrentHashMap();
        User user=new User();
        user.inport=1234;
        user.port=345;
        concurrentHashMap.put(user,user);
        Object u=concurrentHashMap.get(1234&345);

    }
}
