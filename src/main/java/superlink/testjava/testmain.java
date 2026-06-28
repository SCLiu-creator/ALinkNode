package superlink.testjava;

import com.alibaba.fastjson2.JSON;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLResult;
import org.dom4j.io.XMLWriter;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;
import superlink.filemanage.classprocess.AutoScan;
import superlink.filemanage.classprocess.Jarloader;
//import superlink.udpbind.cloude.CloudeListenLocal;
import superlink.udpbind.usedata.User;
import superlink.util.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;


public class testmain {

    public static ThreadLocal<Object> loaclRequest;
    public static ThreadPoolExecutor threadPoolExecutor;
    public static List<BlockingQueue<byte[]>> quelist =new ArrayList<BlockingQueue<byte[]>>();
   // public static BlockingQueue<byte[]> que =new LinkedBlockingQueue<byte[]>(10000);
//pre 8 mtu 1500 ip 1480 udp 1472
    //min mtu 576 udp548
    //pppoe mtu 1452
    public static byte[] b=new byte[65535];//65507(nohead and trd) 8 46 576
    private void bage(){
        System.out.println("aaaaaa");


    }
    public static void main(String[] ages) throws  Exception {
        String sc="a";
        String bc="b";
        int ci=sc.compareTo(bc);
        URL url=null;
        AutoScan.classLoader=AutoScan.class.getClassLoader();
        URL[] url1=new URL[1];
        url1[0]=url;
        Jarloader urlClassLoader=new Jarloader(url1,AutoScan.classLoader);
        urlClassLoader.addURL(url);
        AutoScan.classLoader=urlClassLoader;

        AutoScan.scanJar(new File("D:\\java\\superlink demo\\target\\original-StartMain.jar"),new ArrayList<>());


        RandomAccessFile randomFile=null;
        try {
            randomFile = new RandomAccessFile(new File("C:\\Users\\liushengchang-n\\Desktop\\漏检过检图片.zip"),"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        FileInputStream inputStream=null;
        try {
            inputStream = new FileInputStream(new File("C:\\Users\\liushengchang-n\\Desktop\\漏检过检图片.zip"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        long ti=0;
        ti=System.currentTimeMillis();
        int len;
        byte[] bytef0=new byte[1280];
        byte[] bytef=new byte[1280];
        byte[] bytef1=new byte[1280];
        byte[] bytef2=new byte[1280];

        len=randomFile.read(bytef);
        len=randomFile.read(bytef1);
        len=randomFile.read(bytef2);

        for (int i = 0; i < 1000*1000; i++) {
//            Utils.byteMerger(bytef,bytef1,bytef2);
//            bytef0=Utils.byteMerger(bytef,bytef1);
//            Utils.byteMerger(bytef0,bytef2);
            for (int j = 0; j < 1000; j++) {
                bytef[j]=bytef0[j];
                bytef0[j]=bytef1[j];
//                bytef1[j]=bytef2[j];
            }
        }

        System.out.println(System.currentTimeMillis()-ti);







        Object o=JSON.parseObject("{\"u\":\"mDCD8GCCQIYhER7L\",\"s\":0,\"l\":30}");

        long ls1=System.currentTimeMillis();
        //File filett=new File("C:\\Users\\liusc\\Desktop\\te.xml");
        for (int i = 0; i <1000*1000 ; i++) {
//            File filett=new File("C:\\Users\\liusc\\Desktop");
////            filett.list();
//            SecurityManager security = System.getSecurityManager();
            System.currentTimeMillis();
            //BasicFileAttributeView basicView= Files.getFileAttributeView(filett.toPath(), BasicFileAttributeView.class);
//            basicView.readAttributes().size();
//            basicView.readAttributes().lastModifiedTime();
//            filett.length();
        }
        System.out.println(System.currentTimeMillis()-ls1);
//4411
        String dir;
        String pathname;
        File filex=new File("C:\\Users\\liusc\\Desktop\\te.xml");
        filex.createNewFile();
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("rootpath");//添加根节点
        FileOutputStream fileOutputStreamx=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        String name=filex.getPath();
//        String dirfile=new File(dir).getParent().replace("\\","/");
        //absolutpath
        byte[] bytes64e= Utils.longToByteArray(System.currentTimeMillis());
        StringBuilder stringBuilder=new StringBuilder();
        for (byte b:bytes64e) {
            stringBuilder.append((char)b);
        }
        String b256=new String(bytes64e,"ISO-8859-1");
        byte[] bb256=b256.getBytes();
        long l246=Utils.byteArrayToLong(bb256);
        byte[] bytes64=Base64.getEncoder().encode(bytes64e);
        String b64de=new String(bytes64,"UTF-8");
        byte[]  b64en=Base64.getDecoder().decode(b64de);
        root.addAttribute("a","a&a\"3''");
        //targe
//        root.addElement("a").addAttribute("p","path&quot""a\\""\\""name");
        FileInputStream fileInputStreamx=null;
        try {
            fileOutputStreamx=new FileOutputStream(filex);
            writer=new XMLWriter(fileOutputStreamx);

            writer.write(document);
            writer.close();
            XMLResult xmlResult=null;
//            "D:\\java\\新建文件夹\\udpclient\\data\\cachepath\\90957b9e205ab141c599d11f4236bb94.xml"
            fileInputStreamx=new FileInputStream(
                    "C:\\Users\\liusc\\Desktop\\te.xml");
            saxReader=new SAXReader();
            document=saxReader.read(fileInputStreamx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getRootElement();


        ArrayList<Integer> elementList=new ArrayList();
        elementList.add(9);
        elementList.add(3);
        elementList.add(6);
        elementList.add(7);
        elementList.add(3);
        elementList.add(2);
        elementList.add(6);
        elementList.add(0);
        elementList.add(5);
        elementList.add(7);
        Field field=ArrayList.class.getDeclaredField("elementData");
        field.setAccessible(true);
        Object[] strings= (Object[]) field.get(elementList);
        Integer element1;
        boolean tb=true;
        int j=elementList.size();
        while (tb && j>0){
            tb=false;
            Integer element=elementList.get(0);
            for (int i = 1; i < elementList.size() ; i++) {
                element1=elementList.get(i);
                if (element>=element1){
                    element=element1;
                }else {
                    tb=true;
                    elementList.set(i-1,element1);
                    elementList.set(i,element);
                }
            }
            if (tb==false){
                break;
            }else {
                tb=false;
            }
            element=elementList.get(elementList.size()-1);
            for (int i = elementList.size()-2; i >0 ; i--) {
                element1=elementList.get(i);
                if (element<=element1){
                    element=element1;
                }else {
                    tb=true;
                    elementList.set(i+1,element1);
                    elementList.set(i,element);
                }
            }
            j--;
        }



        byte te=23;
        System.out.println(te&256);
        byte[][] bytesbuf=new byte[6][];
        bytesbuf[0]=new byte[]{11,1,1,3};
        bytesbuf[1]=new byte[]{11,1,1,3};
        bytesbuf[2]=new byte[]{11,1,1,3};
        System.out.println(bytesbuf.length);

//        CloudeListenLocal cloudeListenLocal =new CloudeListenLocal();
//        Thread thread=new Thread(cloudeListenLocal);
//        thread.start();
//        Thread.sleep(1000);
//        synchronized (cloudeListenLocal.Listbin){
//            cloudeListenLocal.Listbin.notify();
//            Thread.sleep(3000);
//            byte[] bytessss=new byte[10];
//            byte[] bytesssss=new byte[10];
//        }

        byte[] bytessss=new byte[10];
        byte[] bytesssss=new byte[10];
        boolean boo=bytessss==bytesssss;


        Map tm=new ConcurrentHashMap();
        tm.put(1,"aaa");
        tm.put(2,"aba");
        tm.put(3,"aca");
        Iterator iterator=tm.keySet().iterator();
        while (iterator.hasNext()){
            Integer integer= (Integer) iterator.next();
            String s=String.valueOf(integer);
            System.out.println(s);
            //iterator.hasNext();
            if (false){break;}
        }

        List<String> lists=new LinkedList<>();
          lists.add("a1aaa");
        lists.add("b1bbb");
        String ssssssss="a1aaaa";
        lists.stream().forEach(S->{ if ("a".equals(S.split("1")[0])){ }});

//        new Thread(()->{
//            ServerSocket serverSocket= null;
//            try {
//                serverSocket = new ServerSocket(7634);
//                serverSocket.setReuseAddress(true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
        DatagramSocket datagramSocketudp=new DatagramSocket();
        datagramSocketudp.send(new DatagramPacket("0000AAAAAAAAAAa".getBytes(),"0000AAAAAAAAAAa".getBytes().length
        ,InetAddress.getByName("192.168.241.236"),8111));//58.241.79.117

        AtomicReference<Socket> tcpsocket = new AtomicReference<>();
        new Thread(()->{

            try {
                System.out.println("star"+Thread.currentThread().getName());
                tcpsocket.set(new Socket());
                tcpsocket.get().setReuseAddress(true);
                tcpsocket.get().bind(new InetSocketAddress(InetAddress.getLocalHost(),7634));
                tcpsocket.get().connect(new InetSocketAddress(InetAddress.getByName("121.36.11.172"),8800));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        AtomicReference<Socket> tcpsocket2 = new AtomicReference<>();
        new Thread(()->{

            try {
                System.out.println("star"+Thread.currentThread().getName());
                tcpsocket2.set(new Socket());
                tcpsocket2.get().setReuseAddress(true);
                tcpsocket2.get().bind(new InetSocketAddress(InetAddress.getLocalHost(),7634));
                tcpsocket2.get().connect(new InetSocketAddress(InetAddress.getByName("121.36.11.172"),8800));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        DatagramSocket datagramSocketbl=new DatagramSocket(8000);


        User usertcp=new User();
        usertcp.inport=7634;
        String tcpse=JSON.toJSONString(usertcp);
        tcpsocket.get().getOutputStream().write(tcpse.getBytes());
        byte[] bytestcp=new byte[1460];
                tcpsocket.get().getInputStream().read(bytestcp);
        System.out.println(new String(bytestcp));
        System.out.println(tcpsocket.get().getReceiveBufferSize());
        int il=1000000000;
        byte[] bbl=new byte[65507];
        byte[] btt=new byte[]{0,0,2,3,0,0};
        byte[] bbbt= Arrays.copyOfRange(btt,2,4);
        System.out.println(bbbt.toString());
        String asc=System.getProperty("file.encoding");
        byte[] bytesasc=new byte[]{74,0,0};
        String asecc=new String(bytesasc, Charset.forName(asc));
        String assss=new String(bytesasc,0,1, Charset.forName(asc));
        boolean basc=asecc.replace("\u0000","").equals(assss.replace("\u0000",""));

        datagramSocketbl.setReceiveBufferSize(65535*10);
        datagramSocketbl.setSoTimeout(3000);
        int ltime=datagramSocketbl.getSoTimeout();

        Integer bufzso=datagramSocketbl.getReceiveBufferSize();
        b[1]=10;
        b[2]=74;
        User userse=new User();

        datagramSocketbl.send(new DatagramPacket(b,655,InetAddress.getLocalHost(),8000));
        b[2]=75;
        datagramSocketbl.send(new DatagramPacket(b,655,InetAddress.getLocalHost(),8000));
        b[2]=76;
        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
        b[2]=76;
        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
        b[2]=76;
        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
        b[2]=76;
        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
        b[2]=76;
        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));


        b[2]=77;
        BlockingQueue<byte[]> que =new LinkedBlockingQueue<byte[]>(10000);
        Unsafe unsafe= CacheDemo.getUnsafe();
       // unsafe.allocateMemory(6000*65507*2);
      //  ByteBuffer byteBuffer=ByteBuffer.allocateDirect(6000*65507*2);

        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                int p=76;
                while (i<10000){

                    i++;
                    try {
                        Thread.sleep(4);
                        byte[] bbl=new byte[65507];
                        b[1]=10;
                        p=p+1;
                        b[2]=(byte) (p+1);
                        datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
                    } catch (IOException | InterruptedException e){
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        DatagramPacket datagramPacketbl=new DatagramPacket(new byte[65507],65507);
        int bl=0;
        while (bl<6000){
            bl++;
            datagramPacketbl.setData(new byte[65507]);
            try{
                datagramSocketbl.receive(datagramPacketbl);
            }catch (SocketTimeoutException e){
                datagramSocketbl.send(new DatagramPacket(b,65507,InetAddress.getLocalHost(),8000));
            }

            Integer bufzso1=datagramSocketbl.getReceiveBufferSize();
        que.add(datagramPacketbl.getData());

        }
        while (bl>0){
            bl--;
            System.out.println(new String(que.take()));
        }



        String aaa="aaa"+(10+001000)+0011111111+0011111111.;
        DirectBuffer directBuffer;
        boolean[] booleans=new boolean[10];
        if (booleans[1]==false){
            System.out.println(booleans[1]);
        }
        int a=0;
        int ab=3377/3377;
        long alo=Long.MAX_VALUE;
        int aint=Integer.MAX_VALUE;
        //a=(int)(alo/aint/4);
        File file1=new File("C:\\Users\\liusc\\Desktop\\20210924002345191.png");
        File file2=new File("C:\\Users\\liusc\\Desktop\\rev.png");
        file2.createNewFile();

        //RandomAccessFile randomAccessFile=new RandomAccessFile(file2,"rw");
        //FileChannel raChannel=randomAccessFile.getChannel();
        FileInputStream fileInputStreamff=new FileInputStream(file1);
        FileChannel fileChannel=fileInputStreamff.getChannel();
        long filelong=fileChannel.size();
        int fileint=Math.toIntExact(filelong);
        ab=(int)(filelong/65507)+1;
        DatagramSocket datagramSocket111=new DatagramSocket(8888);
        byte[] bre=new byte[65507];

        while (a<ab){
            int seek=a*65507;
            a=a+1;
            RandomAccessFile randomAccessFile=new RandomAccessFile(file2,"rw");
            fileInputStreamff.read(bre);
            DatagramPacket datagramPacket111=new DatagramPacket(bre,bre.length,InetAddress.getLocalHost(),8888);
            datagramSocket111.send(datagramPacket111);
            DatagramPacket datagramPacket222=new DatagramPacket(new byte[65507],65507);
            datagramSocket111.receive(datagramPacket222);
            ByteBuffer byteBuffer1=ByteBuffer.wrap(datagramPacket222.getData());
            randomAccessFile.seek(seek);
            randomAccessFile.write(datagramPacket222.getData());
            randomAccessFile.close();
            //raChannel.write(byteBuffer);
        }

       // raChannel.close();




        System.out.println(datagramSocket111.getReceiveBufferSize());
        System.out.println(a);
        StringBuilder AA=new StringBuilder("");
        for (int i=0;i<65508*3;i=i+1){
            AA=AA.append("AA");
        }
        ServerSocket sockettcp=new ServerSocket(8888);

        File file =new File("D:\\BaiduNetdiskDownload\\EasyVtuberPP.zip");
        FileInputStream fileInputStream1 =new FileInputStream(file);
        BufferedInputStream fileInputStream=new BufferedInputStream(fileInputStream1);

        System.out.println(fileInputStream.available());
        fileInputStream.mark(8199);
        byte[] bt={11,11,1,1,0};
        byte[] buffb = new byte[1];
        int bufff=fileInputStream.read();
        buffb[0]= (byte) bufff;
        System.out.println(new String(buffb));
        int buffff=fileInputStream.read();
        int bufffff=fileInputStream.read(bt);
        System.out.println(new String(bt));
        bufffff=fileInputStream.read(bt);
        System.out.println(new String(bt));
        System.out.println(fileInputStream.available());

        fileInputStream.reset();
        //bufffff=fileInputStream.read(bt,2,2);
        System.out.println(new String(bt));
        System.out.println(fileInputStream.available());
        fileInputStream.skip(-5);
        System.out.println(fileInputStream.available());
        bufffff=fileInputStream.read(bt);
        System.out.println(new String(bt));
        System.out.println(fileInputStream.available());
        //b[2]=bt[5];
        ExecutorService threadPoolExecutor1=Executors.newSingleThreadExecutor();
        threadPoolExecutor1.submit(new Runnable() {
            @Override
            public void run() {
               // File file=new File();
                byte[] bytes=getPicture("E:\\spring-tool-suite-4-4.9.0.RELEASE-e4.18.0-win32.win32.x86_64.self-extracting.jar");
                try {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DatagramPacket packet=new DatagramPacket(bytes,bytes.length,InetAddress.getLocalHost(),8889);

                    DatagramSocket socket=new DatagramSocket(8888);

                    socket.send(packet);

                } catch (IOException  e2) {
                    e2.printStackTrace();
                }


            }
        });

        DatagramPacket packetsss=new DatagramPacket(b,b.length);

        DatagramSocket socketsss=new DatagramSocket(8889);
        socketsss.receive(packetsss);
        byte[] bytesss=packetsss.getData();
        toPicture2(bytesss,"C:\\Users\\liushengchang-n\\Desktop\\v2111111.zip");
//
//        DataFactory dataFactory=new DataFactory();
//        testcall dataRecive=dataFactory.getRecive();
//        Integer aaaaaa=3333;
//        UserRequest ssss=dataRecive.Recive1(aaaaaa);
//        String sssss= ssss.username;


        int corePoolSize=4;
        int maximumPoolSize=10;
        long keepAliveTime=60;
        Integer aaaa=11;
        new I1(aaaa).ss();
        User u=new User();
        System.out.println(u);
        I1 i=new I1(u);
        u.inport=10;
        i.sss();
        System.out.println(u);
        System.out.println(JSON.toJSONString(u));
        System.out.println(aaaa);
        BlockingQueue<Runnable> ttt=new ArrayBlockingQueue<>(10);
        threadPoolExecutor=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.MILLISECONDS,ttt);
        byte[] bytes=new byte[10];
        DatagramPacket packet=new DatagramPacket(bytes,10);
        DatagramSocket socket=new DatagramSocket(8888);
        Trn t=new Trn(3,socket);
        Trn t1=new Trn(4,socket);
        Trn t2=new Trn(5,socket);
        Trn t3=new Trn(6,socket);
        ExecutorService ex=Executors.newFixedThreadPool(5);
        Future fu=ex.submit((Callable<Integer>)t);
        Integer ant= (Integer) fu.get();
        System.out.println(ant);
        ex.execute(t);
        Future f=threadPoolExecutor.submit((Callable<? extends Object>) t);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket1=new DatagramSocket(8889);
                    DatagramPacket d=new DatagramPacket("new byte[10]".getBytes(),10,InetAddress.getLocalHost(),8888);
                    while (true){
                        //System.out.println("readlysend");
                        socket.send(d);
                        //System.out.println("sendafter");
                        //JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        threadPoolExecutor.execute(t);
        threadPoolExecutor.execute(t1);
        threadPoolExecutor.execute(t2);
        threadPoolExecutor.execute(t3);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            t.wait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loaclRequest=new ThreadLocal<>();
        System.out.println(JSON.toJSONString(Thread.currentThread()));
        loaclRequest.set(new Trn(1,socket));
        loaclRequest.set(new Trn(2,socket));
        System.out.println(loaclRequest.get());


        int[] ii={1};
        String send="OC"+ 10000000;
        Object lock=new Object();
        DatagramPacket datagramPacket=new DatagramPacket("AAAAAAAAAAAAAAAAAAAAAAAAA".getBytes(),0,"AAAAAAAA".getBytes().length+10);

        DatagramSocket d=new DatagramSocket(7889, InetAddress.getLocalHost());
        new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    datagramPacket.setData("12345678".getBytes(),0,8);
                    SocketAddress s=new InetSocketAddress(InetAddress.getLocalHost(),7889);
                    datagramPacket.setSocketAddress(s);
                    d.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
        DatagramPacket packet1=new DatagramPacket(new byte[8],2);
        d.receive(packet1);
        String s=new String(packet1.getData(),0,8);
        System.out.println(s);

      testc tt=new testc();
        try {
            try {
                tt.tt();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        byte[] bytes2=getPicture("C:\\Users\\liushengchang-n\\Desktop\\7211.zip");
        byte[] bytes1=new byte[10];
        byte[] bb=byteMerger(bytes2,bytes1);
        byte[] bbb=byteMerger(bytes1,bytes2);
        byte[] bbbb=byteMerger(bytes2,bytes2);
        toPicture2(bb,"C:\\Users\\liushengchang-n\\Desktop\\72111.zip");
        toPicture2(bbb,"C:\\Users\\liushengchang-n\\Desktop\\7212.zip");
        toPicture2(bbbb,"C:\\Users\\liushengchang-n\\Desktop\\7213.zip");
    }
    public static byte[] getPicture(String s) {//提取图片
        File file = new File(s);//"C:/a"
        byte[] bytes=null;
        try {
            FileInputStream fos = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fos);
            ByteBuffer byteBuffer;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = fos.read(buff)) != -1) {

                byteArrayOutputStream.write(buff, 0, len);
            }
            ByteArrayInputStream arrayInputStream=new ByteArrayInputStream(new byte[10]);

            bytes= byteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            e.printStackTrace();
        }return bytes;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
    public static void toPicture2(byte[] data,String name){//保存为图片
        File file=new File(name);

        try {
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] imgbyte = decoder.decodeBuffer("刚刚将字节数组转成的字符串");
            OutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void test1() throws Exception {
        // 利用通道完成文件的复制(非直接缓冲区)
        FileInputStream fis = new FileInputStream("a.txt");
        FileOutputStream fos = new FileOutputStream("b.txt");
        // 获取通道
        FileChannel fisChannel = fis.getChannel();
        FileChannel foschannel = fos.getChannel();

        // 通道没有办法传输数据，必须依赖缓冲区
        // 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 将通道中的数据存入缓冲区中
        while (fisChannel.read(byteBuffer) != -1) {  // fisChannel 中的数据读到 byteBuffer 缓冲区中
            byteBuffer.flip();  // 切换成读数据模式
            // 将缓冲区中的数据写入通道
            foschannel.write(byteBuffer);
            byteBuffer.clear();  // 清空缓冲区
        }
        foschannel.close();
        fisChannel.close();
        fos.close();
        fis.close();
    }

    public static void test2() throws Exception {
        // 使用直接缓冲区完成文件的复制(内存映射文件)
        /**
         * 使用 open 方法来获取通道
         * 需要两个参数
         * 参数1：Path 是 JDK1.7 以后给我们提供的一个类，代表文件路径
         * 参数2：Option  就是针对这个文件想要做什么样的操作
         *      --StandardOpenOption.READ ：读模式
         *      --StandardOpenOption.WRITE ：写模式
         *      --StandardOpenOption.CREATE ：如果文件不存在就创建，存在就覆盖
         */
        FileChannel inChannel = FileChannel.open(Paths.get("a.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("c.txt"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE);

        /**
         * 内存映射文件
         * 这种方式缓冲区是直接建立在物理内存之上的
         * 所以我们就不需要通道了
         */
        MappedByteBuffer inMapped = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapped = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        // 直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMapped.limit()];
        inMapped.get(dst);  // 把数据读取到 dst 这个字节数组中去
        outMapped.put(dst); // 把字节数组中的数据写出去

        inChannel.close();
        outChannel.close();
    }

    public static void test3() throws Exception {
        /**
         * 通道之间的数据传输（直接缓冲区的方式）
         * transferFrom
         * transferTo
         */
        FileChannel inChannel = FileChannel.open(Paths.get("a.txt"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("d.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
        inChannel.transferTo(0, inChannel.size(), outChannel);
        // 或者可以使用下面这种方式
        //outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChannel.close();
    }

    public static void test4() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("a.txt", "rw");
        // 获取通道
        FileChannel channel = raf.getChannel();
        // 分配指定大小缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(2);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        // 分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);  // 参数需要一个数组
        for (ByteBuffer byteBuffer : bufs) {
            byteBuffer.flip();  // 切换到读模式
        }
        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));  // 打印 he
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));  // 打印 llo

        // 聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("e.txt","rw");
        // 获取通道
        FileChannel channel2 = raf2.getChannel();
        channel2.write(bufs);  // 把 bufs 里面的几个缓冲区聚集到 channel2 这个通道中，聚集到通道中，也就是到了 e.txt 文件中
        channel2.close();
    }
}
