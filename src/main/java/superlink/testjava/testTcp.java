package superlink.testjava;

import superlink.util.Utils;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

public class testTcp {





    byte[] prex=new byte[6];
    DatagramSocket socket;
    DatagramPacket datagramPacket;
    byte[] bytes=new byte[65507];


    {
        try {
            datagramPacket=new DatagramPacket(bytes,65507,InetAddress.getLocalHost(),90);
            socket = new DatagramSocket(80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data){
        byte[] send=Utils.byteMerger(prex,data);
        datagramPacket.setData(send,0,send.length);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void  send0(byte[] data){

//        byte[] send=Utils.byteMerger(prex,data);
        System.arraycopy(data,0,bytes,6,data.length);
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        // synchronized (datagramPacket){
        datagramPacket.setData(bytes,0,data.length+6);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
    }
    public static void main(String[] args) throws IOException {
//        byte[] bytes=;
        System.out.print(new String(new byte[]{66,65,-1,-23}));
        String sps="abcde/fabcdabceg/hjab";
        String[] stringss1=sps.split("ab");
        String[] stringss2=sps.split("abc");
        String[] stringss3=sps.split("cdef",2);
        String[] stringss4=sps.split("ab",1);
        String[] stringss5=sps.split("l",2);
        sps=sps.substring(sps.lastIndexOf("/")+1);

    String s=":1981:::2:2:2";
        String[] strings=s.split(":");
        Thread thread=new Thread(()->{System.out.println("aaaa");
        });
        thread.start();
        thread.start();


        long t1=System.currentTimeMillis();
        testTcp testTcp=new testTcp();
        int f=256*256*256;
        byte[][] bytess=new byte[20000000][];
//        byte[][] bytess1=new byte[20000000][];
//        byte[][] bytess2=new byte[20000000][];
//        byte[][] bytess3=new byte[20000000][];
//        byte[][] bytess4=new byte[20000000][];

        for (byte[] b:bytess){
            if (b==null){
                testTcp.send(new byte[1450]);
            }
        }
        t1=System.currentTimeMillis()-t1;
        System.out.println(t1);
        byte b= (byte) 0b00000001;

        byte b2= (byte) (b<<7);
        byte b3= (byte) (b&0b0000100);
        int ib=b;


        ServerSocket serverSockett=new ServerSocket(9090);
        new Thread(()->{
            try {
                Thread.sleep(1000);
                new Socket().connect(new InetSocketAddress(9090));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
        InputStream stream=serverSockett.accept().getInputStream();
        boolean bt=true;
        while (bt){
            stream.read(new byte[1200]);
            System.out.println("aaaa");
        }

        byte[] bytes0=new byte[65507];
        byte[] bytest=new byte[65507];
        int i=100000000;
        i=10000000;

        //13877 16066       68367
        //35399 34485     26969
        //21897             69030
        //22250
//        testTcp testTcp=new testTcp();
        //7012 6740
        //6444
        //6555
        while (i>0){
//            testTcp.send0(bytest);
                        bytest=new byte[65507];
//            System.arraycopy(bytes0,0,bytest,0,bytes0.length);
//            bytest= Arrays.copyOfRange(bytes0,0,bytes0.length);
//            bytest= Utils.subByte(bytes0,0,bytes0.length);
            i--;
        }
        //506
        t1=System.currentTimeMillis()-t1;
        System.out.println(t1);




        int l = 0;
        try {


            ServerSocket serverSocket = new ServerSocket(8091,50,InetAddress.getByName("127.0.0.1"));
            AtomicReference<Socket> socketweb= new AtomicReference<>(serverSocket.accept());
            AtomicReference<OutputStream> webout = new AtomicReference<>(socketweb.get().getOutputStream());
            AtomicReference<InputStream> webinput= new AtomicReference<>(socketweb.get().getInputStream());
            new Thread(()->{
                //while (true){
                    try {
                        socketweb.set(serverSocket.accept());
                        webout.set(socketweb.get().getOutputStream());
                        webinput.set(socketweb.get().getInputStream());
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                //}
            }).start();




            Socket socket = new Socket();
            SocketAddress socketAddress=new InetSocketAddress(InetAddress.getByName("127.0.0.1"),7860);//127.0.0.1

//            socket = new Socket("blog.csdn.net", 8080);
            socket.connect(socketAddress);
            System.out.println("socket.getInetAddress(): "+socket.getLocalAddress().toString()+"   "+socket.getLocalPort()+"   "+socket.getInetAddress());

            String http =
                "GET / HTTP/1.1" + "\r" + "\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + "\r" + "\n" +
                "Accept-Encoding:  gzip,deflate, br" + "\r" + "\n" +//
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6" + "\r" + "\n" +
                "Cache-Control: max-age=0" + "\r" + "\n" +
                "Connection: keep-alive" + "\r" + "\n" +
                "DNT: 1" + "\r" + "\n" +
//                "Cookie: uuid_tt_dd=10_36635579180-1621928339490-766178; UN=qq_44916048; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_36635579180-1621928339490-766178!5744*1*qq_44916048; _ga_VHSCGE70LW=GS1.1.1643268970.2.0.1643268970.0; _ga=GA1.2.1131064177.1641265910; c_segment=2; dc_sid=9f9f8753252eec8a7c74c6d1eed953ac; Hm_lpvt_e5ef47b9f471504959267fd614d579cd=1656662751; __bid_n=183f9ebc92629f1bd94207; FPTOKEN=30$r6r9QV+DF5O8k90XA7s7Qx0OwjYVwDQRzClU+CUDwU5BMXzlBer0SsCH60scdKP9IRGBZdQasRqBJ39eKb9iOdSkbU1AAdlw6gpX/ZbAK8f9+wUrevnlpmiP5LiAqbJVXdFYmQereYpgIrB5Q5Lm0sAz/EXHq50JPZ56/JEnphwam5uGRkeUQ5PrODlE/LMDL5348ilc7ro4IZnKYhLyzrXNf12TRu4dPxtIwGFuONHxqfKVG1JpUO/8+6zGa8BRoDkq03TYtC5zy/uKbPjxsSQSAJujph7HAFb/txhr5aVnNmxwOKiL9iMl+zFgyBVN5hO04OYEc5G9hO9TkooY+6gNoY0VuPP6/pv/rxEcHvkxSJVU6F3Z4hFaSzLeYq+D|/YCmyRSzTbisGX8JPYHduQpPC3Kzk+maioxNWujLwBw=|10|7d16df54a8ba2ea379def226356e6e6e; HWWAFSESID=fcfbd589e45ebecf2f3; HWWAFSESTIME=1668600829767; FEID=v10-5180fce79236ee94476816e364748a4932f16a76; __xaf_fpstarttimer__=1673008875293; __xaf_fptokentimer__=1673008875440; __xaf_thstime__=1673010039871; hasShowRRGuideBottom=1; ssxmod_itna=Yq0x2DgD0AAxuDl4iwo8D9iKuRC=xWqbexExDsKhrDSxGKidDqxBWWlehbnGRiYPeDCqfGbbBTpiPfK+xx35Psm+pPDHxY=DUZ7ieQrDeW=D5xGoDPxDeDADYoUDAqiOD7qDdjpkVUkDm4GW7qGfDDoDY+=uDitD4qDBzrdDKqGg7qw5WG6ecf2SfCU5bnDq7qDMUeGX4hctwjcHa7XedZWxr2QDzwHDtMUSBwL3x0pySqNxMO4WBY4z3Y+iWE3TIih50743tZLvRI5Y4xqu544djG59xD34YYSdeD==; ssxmod_itna2=Yq0x2DgD0AAxuDl4iwo8D9iKuRC=xWqbex4ikvoqDl=EQDjRuGrl3L6ZuTzjjhojmxeqL5uIOB7ji2Ii0aRxN+SCR=w7b8D0=jYnQddvYZITxyXizzk=FuKuekMg0PO6clyUyCq/vv+SxjDbhCnoKLlMQmbMjSKkL5HuK3rQI5puRVSMReNd=G/4GFY8Qb2S2Ab=us0iYTZwat=7ob1ec/9wPb=0oei1t9tqyM03wzqoQz/uHPC4wS36dk4ruzvDUrMpfk4dOsrIHsx5lb50ExB9nG5AtwQZUaMZykitzkXrvB288sSwx+bDsWY7r8i98Wui+rDpTqVw=4A4qFRbwTtwQt5TDvr3usmxU/T8lpujhY5DqWFQ3OBm2pYpT0KE9b4LvQ07/iRVlctQENd9UCKKrpSK3w+5iCKFz9NW06C4EC7Xi+j39Fj8VCa2iIktxDKMiD7=DYI4eD==; FPTOKEN=/mLMDhKBUT0k4/KsRRefnoAKn6eDrXGZ6PpxjzvlmqStKJN/NgSmnA6BQtBk0PoRo1yt8Mgx8JQyWj7FOT602k9zRwSpyrIaE3bZTFmLKKL4BritZejvr8TkoDzentYQChf90PwuIGV1WSuUyoe5IsJ9kjhYysKo7Cza6TrMRTK3T7vLrkvmq4ve6WFExggko0IdaWD/zj6WbLG1FiWCnT80jW+xqYQb9Cjr5cE/xv7gL9gEilgjp2+Z5fW5CYAq3BY4k8/QsaYG5iFUgGUkEcyYRg4D943kzMiBEwxKPzY0AKDeFFdO7An+ZgT3AeWqMEL1J5mzmby9HxHsda/rWFQyzROB32+zotdW+WxWxUcAJCScPc8kecqKfjiQ5q7NGsg5I1i7NfYW3G9HJJIO+g==|yuZy10u3OOelIScRSOqfvi/0SyjxAtmnC/Tz5QqA2zE=|10|b4121d89a712b35d992748b10de3c097; FCNEC=%5B%5B%22AKsRol_iTmzgXmGGHQmNZ7nPXNKzZ3TJAq9sq9lMoiYiocx6ONDyfesvQY9T8gGjHQgyx1iaKL1iAK-Bodz9cNyLEpDTl8zhqBvw7bSKN1eQ0T4PFJmI4c_eRPVEA6kOd-HtrFUT21wE-Vp0fGa5DxUHI9KBxdq0fA%3D%3D%22%5D%2Cnull%2C%5B%5D%5D; log_Id_view=11311; c_segment=2; SESSION=49c38c81-194c-428c-9941-31f88072d125; UserName=qq_44916048; UserInfo=9d58aa03304e4871b8a4575f1c4e197b; UserToken=9d58aa03304e4871b8a4575f1c4e197b; UserNick=%E7%A5%9E%E9%A3%8F; AU=283; BT=1691398852719; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_44916048%22%2C%22scope%22%3A1%7D%7D; c_pref=https%3A//blog.csdn.net/w605283073/article/details/103841999; c_ref=https%3A//blog.csdn.net/sayyy/article/details/81120749; log_Id_pv=2886; log_Id_click=1716; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1694653873; c_dl_fpage=/download/weixin_42129113/15019066; c_dl_prid=1695034611492_167357; c_dl_rid=1695035375338_415363; c_dl_fref=https://blog.csdn.net/Think_Java_1993/article/details/8115225; c_dl_um=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-22670851-blog-8115225.235%5Ev38%5Epc_relevant_anti_t3_base; historyList-new=%5B%5D; c_pref=https%3A//blog.csdn.net/sayyy/article/details/81120749; log_Id_click=1717; log_Id_pv=2887; c_ref=https%3A//cn.bing.com/; c_first_ref=cn.bing.com; c_first_page=https%3A//blog.csdn.net/qq_20545159/article/details/43929657; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1695350498; is_advert=1; write_guide_show=1; __gads=ID=341a7c0d38d4f1ea-220e1e26afe70009:T=1689585449:RT=1695350497:S=ALNI_MZ-YtxbiA6I77UZqiXcOEhh_JIyZA; __gpi=UID=00000c09b55d13a7:T=1684809279:RT=1695350497:S=ALNI_MZ_uqYUxYGBHoBi6fCODvzwg1L0yg; dc_tos=s1dnry; dc_session_id=10_1695369889144.529958"
//                +"\r"+"\n"+
                //"Host: blog.csdn.net" + "\r" + "\n" +
                "Host: localhost:7860"+"\r" + "\n" +
               // "Referer: https://cn.bing.com/"+ "\r" + "\n" +
                "Sec-Fetch-Dest: document" + "\r" + "\n" +
                "Sec-Fetch-Mode: navigate" + "\r" + "\n" +
                "Sec-Fetch-Site: none" + "\r" + "\n" +
                "Sec-Fetch-User: ?1" + "\r" + "\n" +
                "Upgrade-Insecure-Requests: 1" + "\r" + "\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81" + "\r" + "\n" +
                "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Microsoft Edge\";v=\"116\"" + "\r" + "\n" +
                "sec-ch-ua-mobile: ?0" + "\r" + "\n" +
                "sec-ch-ua-platform: \"Windows\"" + "\r" + "\n" + "\r" + "\n";
// /r/n0/r/n/r/n
            String csdn="GET /qq_20545159/article/details/43929657 HTTP/1.1\r\n" +
                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                    "Accept-Encoding: gzip, deflate, br\r\n" +
                    "Accept-Language: zh-CN,zh;q=0.9\r\n" +
                    "Connection: keep-alive\r\n" +
                    "Host: blog.csdn.net\r\n" +
                    "Sec-Fetch-Dest: document\r\n" +
                    "Sec-Fetch-Mode: navigate\r\n" +
                    "Sec-Fetch-Site: none\r\n" +
                    "Sec-Fetch-User: ?1\r\n" +
                    "Upgrade-Insecure-Requests: 1\r\n" +
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36\r\n" +
                    "sec-ch-ua: \"Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117\"\r\n" +
                    "sec-ch-ua-mobile: ?0\r\n" +
                    "sec-ch-ua-platform: \"Windows\"\r\n\r\n";
           // socket.getOutputStream().write(http.getBytes());
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();
            outputStream.write(http.getBytes());
//        InputStreamReader inputStreamReader= new InputStreamReader(inputStream);
//        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
       //     String s;
                 new Thread(()->{
                     try {
                         byte[] bytes = new byte[1412];
                         int len2 = 0;
                         StringBuilder stringBuilder=new StringBuilder();
                         while (true){
                             while ((len2 = webinput.get().read(bytes)) != -1) {
                                 //byte[] send = new byte[len2];
                                 String sb=new String(bytes,0,len2);
                                 stringBuilder.append(sb);
                                 //System.arraycopy(bytes, 0, send, 0, len2);

                                 System.out.println(new String(bytes));//9 9 101 2

                             }
                             outputStream.write(stringBuilder.toString().getBytes());

                             //System.out.println("web:   "+stringBuilder.toString());
                             try {
                                 Thread.sleep(500);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                             break;
                         }


                     }catch (IOException e){
                         e.printStackTrace();
                     }


                        });//.start()
            byte[] bytes = new byte[1024];

            while (true) {

                int len = 0;
                StringBuilder Builder = new StringBuilder();
                while ((len = inputStream.read(bytes)) != -1) {
                    l++;
                    String sb = new String(bytes, 0, len,"ISO-8859-1");
                    System.out.println(sb);
                    Builder.append(sb);
                    byte[] send = new byte[len];
                    System.arraycopy(bytes, 0, send, 0, len);

                    //webout.get().write(send);
                    //System.out.println(new String(bytes));//9 9 101 2
                }
//                webout.get().flush();
//                webout.get().close();
                String buf=Builder.toString().replace("/qq_20545159/article/details/43929657","/");
                webout.get().write(buf.getBytes("ISO-8859-1"));

                byte[] bz=Builder.toString().split("\r\n\r\n")[1].getBytes("ISO-8859-1");
                System.out.println(Builder.toString().split("\r\n\r\n")[0]);
                GZIPInputStream g=new GZIPInputStream(new ByteArrayInputStream(bz));
                byte[] bytes1=new byte[256];
                System.out.println("         prase:   ");
                len=0;
                while ((len=g.read(bytes1))!=-1){
                    byte[] bufz=new byte[len];
                    System.arraycopy(bytes1,0,bufz,0,len);
                    System.out.println("ISO-8859-1: "+new String(bufz,"ISO-8859-1"));
                    System.out.println("UTF-8:      "+new String(bufz,"UTF-8"));
                }
               // System.out.println("server:  "+Builder.toString());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
//                int len2 = 0;
//                StringBuilder stringBuilder=new StringBuilder();
//                while ((len2 = webinput.read(bytes)) != -1) {
//                    //byte[] send = new byte[len2];
//                    String sb=new String(bytes,0,len2);
//                    stringBuilder.append(sb);
//                    //System.arraycopy(bytes, 0, send, 0, len2);
//
//                    System.out.println(new String(bytes));//9 9 101 2
//                }
//                outputStream.write(stringBuilder.toString().getBytes());
//                System.out.println(stringBuilder.toString());
//            }
        //    }
       //     webout.write(("\r"+"\n"+"\r"+"\n").getBytes());
            BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
            bufferedInputStream.read();
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("length:  "+l);

//        stream.write(("\r"+"\n"+"\r"+"\n").getBytes());
//        stream.flush();
//        stream.close();



//        while (inputStream.read(bytes)!=-1){
//            System.out.println(new String(bytes));
//        }
    }
}
