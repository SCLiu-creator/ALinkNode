package superlink.testjava;


import superlink.util.Utils;

import java.net.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class trac {

    String tracert = "tracert -h 10 "; //模拟tracert命令

    String ping = "ping";//模拟 ping 命令

    String routePrint = "route print -4";//模拟route print命令

//    注意：这边我是用while循环主要实现tracert以及ping命令要是读者需要
//    完全重写dos命令行模式则需要使用多线程并采用同步原理，虽然工程量大
//    但是实现原理并不难，读者可自行尝试。
//39.144-147.xxx.xxx 移动/数据上网公共出口
    public static void main(String args[]){
        int has=Objects.hash("1234","123333",1323,null);
        int has1=Objects.hash(new String("1234".getBytes()),new String("123333".getBytes()),1323,null);
        byte[] bytes=Utils.intToByteArray(234);
        bytes=Utils.intToByteArray(-12333);
        long l=Utils.calculateChecksum(bytes,0,4);
        bytes=Utils.longToByteArray(l);
        bytes=Utils.intToByteArray(256*256*256);
        l=Utils.calculateChecksum(bytes,0,bytes.length);
        bytes=Utils.longToByteArray(l);
        bytes=("//    注意：这边我是用while循环主要实现tracert以及ping命令要是读者需要\n" +
                "//    完全重写dos命令行模式则需要使用多线程并采用同步原理，虽然工程量大\n" +
                "//    但是实现原理并不难，读者可自行尝试。\n" +
                "//39.144-147.xxx.xxx 移动/数据上网公共出口").getBytes();
        l=Utils.calculateChecksum(bytes,0,bytes.length);
        bytes=Utils.longToByteArray(l);
        bytes="D".getBytes();
        bytes="DI".getBytes();
        int i0=1;
        String ss="aaaa";
        long lo=4;
        String sn=null;
        int hh1=Objects.hash(i0,ss,lo,sn);
        int hh2=Objects.hash(i0,sn,ss,lo);
        Map hashMap=new ConcurrentHashMap();
        Set set = hashMap.entrySet();
        hashMap.put(1,4);
        set = hashMap.keySet();
//        set.add(9);
        hashMap.put(3,5);
        set = hashMap.entrySet();
        hashMap.put(2,48);
        final Integer[] i = {1};
        hashMap.forEach((k,v)->{
            hashMap.put(3,8);
            i[0]++;
            hashMap.remove(i[0]);

        });
        set = hashMap.entrySet();
        hashMap.remove(1);
        set = hashMap.entrySet();
        hashMap.remove(3);
        set = hashMap.entrySet();
        hashMap.entrySet().remove(48);
        set = hashMap.entrySet();
        set=new HashSet();
        Iterator iterator=set.iterator();
        iterator.hasNext();
        iterator.next();
        String input = null;
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        //利用while循环接收输入的命令行参数
        while(true){

            System.out.println("Please input destination server IP address ：\n");
//39.144.154.244
            input ="122.51.51.35";//scanner.next();

            trac host = new trac();
            host.tracert = host.tracert + " " + input;
            host.ping = host.ping + " " + input;

            try {

                host.command(host.routePrint);
                host.command(host.tracert) ;
                host.command(host.ping);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            InetAddress ipAddress;

            try {

                ipAddress = InetAddress.getByName(input);

                System.out.println("IP address : "+ipAddress);

            } catch (UnknownHostException exception) {
                exception.printStackTrace();
            }

            URL url;

            try {
                url = new URL("http",input,80,"index.html");

                System.out.println();//输出服务器地址

                System.out.println("Get the Server-Name# : "+url.getHost());

                System.out.println();//输出首页文件

                System.out.println("Get the default file# : "+url.getFile());

                System.out.println();//输出首页协议和端口

                System.out.println("Get the protocol# : "+url.getProtocol()+" "+url.getPort());

                System.out.println();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            System.out.println();

            try {

                System.out.println("Get serverName & IPAddress："+InetAddress.getByName(input));
            } catch (UnknownHostException e) {

                e.printStackTrace();
            }

            long freeMemory = Runtime.getRuntime().freeMemory();

            System.out.println("Surplus memory of JVM: "+freeMemory+"B");

        }
    }
    //模拟 tracert 命令
//
//    注意这边将跃点数定为10个，也可以不在内部定义，而在命令输入时确定
//    为了方便起见，直接将ping命令和tracert命令封装在一起，
//    这样子做的好处就是既能够显示每一个数据报经过的路由，也能显示是否到达以及耗时
//    可以在ping命令中自己设置TTL值以及要发送的数据包的数量，读者自己选择即可



    StringBuffer commandResult = null;

    private void command(String tracerCommand) throws IOException{
        //第一步：创建进程(是接口不必初始化)

        //1.通过Runtime类的getRuntime().exec()传入需要运行的命令参数

        System.out.println();

        System.out.println(InetAddress.getByName("localhost")+" is tracking the destination server...");

        Process process = Runtime.getRuntime().exec(tracerCommand);

        readResult(process.getInputStream());

        process.destroy();
    }
    //第二步：通过输入流来将命令执行结果输出到控制台

    private void readResult(InputStream inputStream) throws IOException{

        commandResult = new StringBuffer();  //初始化命令行

        String commandInfo = null; //定义用于接收命令行执行结果的字符串

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream,"GBK"));

        while ( (commandInfo = bufferedReader.readLine()) != null)  {
           // URLDecoder.decode( commandInfo,"ISO-8859-1")
            System.out.println(commandInfo);
        }
        bufferedReader.close();
    }
}



//以下是运行结果：
/*
Please input destination server IP address ：
www.sina.com
localhost/127.0.0.1 is tracking the destination server...
===========================================================================
接口列表
 17...50 b7 c3 ea 61 71 ......Qualcomm Atheros AR9485WB-EG Wireless Network Adapter
 14...20 89 84 17 3e 21 ......Realtek PCIe GBE Family Controller
 13...50 b7 c3 ea 61 72 ......Bluetooth 设备(个人区域网)
  1...........................Software Loopback Interface 1
 25...00 00 00 00 00 00 00 e0 Microsoft ISATAP Adapter #2
 24...00 00 00 00 00 00 00 e0 Microsoft ISATAP Adapter #4
 23...00 00 00 00 00 00 00 e0 Microsoft ISATAP Adapter #5
 21...00 00 00 00 00 00 00 e0 Teredo Tunneling Pseudo-Interface
===========================================================================
IPv4 路由表
===========================================================================
活动路由:
网络目标        网络掩码          网关       接口   跃点数
          0.0.0.0          0.0.0.0     10.12.31.254      10.12.20.73     25
        10.12.0.0    255.255.224.0            在链路上       10.12.20.73    281
      10.12.20.73  255.255.255.255            在链路上       10.12.20.73    281
     10.12.31.255  255.255.255.255            在链路上       10.12.20.73    281
        127.0.0.0        255.0.0.0            在链路上         127.0.0.1    306
        127.0.0.1  255.255.255.255            在链路上         127.0.0.1    306
  127.255.255.255  255.255.255.255            在链路上         127.0.0.1    306
        224.0.0.0        240.0.0.0            在链路上         127.0.0.1    306
        224.0.0.0        240.0.0.0            在链路上       10.12.20.73    281
  255.255.255.255  255.255.255.255            在链路上         127.0.0.1    306
  255.255.255.255  255.255.255.255            在链路上       10.12.20.73    281
===========================================================================
永久路由:
  无
localhost/127.0.0.1 is tracking the destination server...
通过最多 10 个跃点跟踪
到 cernetnews.sina.com.cn [121.194.0.239] 的路由:
  1    13 ms    14 ms     2 ms  10.12.31.254
  2     3 ms     7 ms     4 ms  10.11.7.33
  3     4 ms     3 ms     2 ms  10.11.7.18
  4    15 ms     6 ms     5 ms  202.112.5.133
  5     9 ms    10 ms    22 ms  101.4.118.73
  6    15 ms    13 ms    43 ms  202.112.38.166
  7     3 ms     8 ms     4 ms  D3-CER-7609.IDC.EDU.CN [121.194.15.253]
  8     *        *        *     请求超时。
  9     5 ms    10 ms    14 ms  121.194.0.239
跟踪完成。
localhost/127.0.0.1 is tracking the destination server...
正在 Ping cernetnews.sina.com.cn [121.194.0.239] 具有 32 字节的数据:
来自 121.194.0.239 的回复: 字节=32 时间=21ms TTL=55
来自 121.194.0.239 的回复: 字节=32 时间=5ms TTL=55
来自 121.194.0.239 的回复: 字节=32 时间=11ms TTL=55
来自 121.194.0.239 的回复: 字节=32 时间=3ms TTL=55
121.194.0.239 的 Ping 统计信息:
    数据包: 已发送 = 4，已接收 = 4，丢失 = 0 (0% 丢失)，
往返行程的估计时间(以毫秒为单位):
    最短 = 3ms，最长 = 21ms，平均 = 10ms
IP address : www.sina.com/121.194.0.239
Get the Server-Name# www.buu.edu.cn
Get the default file# index.html
Get the protocol# http 80
Get serverName & IPAddress：www.sina.com/121.194.0.239
Surplus memory of JVM: 54469248B
Please input destination server IP address ：
*/