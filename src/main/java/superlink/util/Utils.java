package superlink.util;

import superlink.filemanage.scanpackage.FileScan;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.ProcessMapL;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.util.ImageTool.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static superlink.udpbind.client.UDPclient.mainDataQueue;
import static superlink.udpbind.client.UDPclient.userlocal;

public class Utils {
    public static boolean isEmpty(String content) {
        if (content==null){
            return true;
        }else {
            return false;
        }
    }

    public void dnslocal() {
        // 这么一来我们的hostName域名都将交给`114.114.114.114`去帮我们完成解析
        System.setProperty("sun.net.spi.nameservice.nameservers", "114.114.114.114");

        // 若你没指定此key，那就是default。默认就会使用系统自带的DNS
// `dns,sun`的意思是：会使用`sun.net.spi.nameservice.nameservers`配置指定的DNS来解析
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
    }

    //获取本地ip
    //32byte
    public static InetAddress getLocalIpv4() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        inetAddress.getHostAddress().toString();
                        return inetAddress;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InetAddress getLocalIpv6() {
        InetAddress inetAddress = null;
        try {
            
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress= enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        // 跳过回环接口和虚拟接口
                        if (intf.isLoopback() || intf.isVirtual()) {
                            continue;
                        }


                        System.out.println(inetAddress.getHostAddress().toString());
                        if (inetAddress.getHostAddress().toString().contains("%")){
                            String s=inetAddress.getHostAddress().split("%")[0];
                            inetAddress=InetAddress.getByName(s);
                        }
                        if (inetAddress instanceof Inet6Address && !inetAddress.isLinkLocalAddress()) {
                            System.out.println("IPv6 Address: " + inetAddress.getHostAddress());
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    //获取本地ip
    //128byte
    public static String getLocalIp6() {

        InetAddress inetAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        outer:
        while (networkInterfaces.hasMoreElements()) {
            Enumeration<InetAddress> inetAds =
                    networkInterfaces.nextElement()
                            .getInetAddresses();
            while (inetAds.hasMoreElements()) {
                inetAddress = inetAds.nextElement();
                //Check if it's ipv6 address and reserved address
                if (inetAddress instanceof Inet6Address
                        && !(inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress())) {
                    break outer;
                }
            }
        }
        String ipAddr = inetAddress.getHostAddress();
        // Filter network card No
        int index = ipAddr.indexOf('%');
        if (index > 0) {
            ipAddr = ipAddr.substring(0, index);
        }
        try {
            InetAddress inetAddress1 = InetAddress.getByName(ipAddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddr;

    }

    public static DatagramSocket getUdpSocket() {
        while (true) {
            try {
                Random randomport = new Random();
                int port = randomport.nextInt(3000) + 6000;
                return new DatagramSocket(port);
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("port already in use");
            }
        }
    }


//    Calendar calendar = Calendar.getInstance();
//    int hours = calendar.get(Calendar.HOUR_OF_DAY); // 时
//    int minutes = calendar.get(Calendar.MINUTE);    // 分
//    int seconds = calendar.get(Calendar.SECOND);
    public static long calculateChecksum(byte[] bytes) {
        return calculateChecksum(bytes,0,bytes.length);
    }

    public static long calculateChecksum(byte[] bytes, int startPos,int length) {
//        bytes=subByte(bytes,startPos,length);
//        Adler32 adler32=new Adler32();
//        adler32.getValue();
        CRC32 crc = new CRC32();
//        crc.update(bytes);
        crc.update(bytes,startPos,length);
        return crc.getValue();
    }

    /**
     * 截取byte数组   不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        if (b.length < (off + length)) {
            b1 = new byte[b.length - off];
            System.arraycopy(b, off, b1, 0, b.length - off);
        } else {
            System.arraycopy(b, off, b1, 0, length);
        }

        return b1;
    }

    /**
     * 合并byte[]数组 （不改变原数组）
     *
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] byteMerger(byte[] ... bytess) {
        int length = 0;
        int len = 0;
        for (byte[] bytes:bytess){
            length=length+bytes.length;
        }
        byte[] send=new byte[length];
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,send,len,bytes.length);
            len=len+bytes.length;
        }
        return send;
    }


    /**
     * @param userid
     * @param id
     * @return 合并后的byte
     */
    public static byte[] getUseridByte(int userid, short id) {
        byte[] bytes = new byte[6];
        bytes[0] = (byte) ((userid >> 24) & 0xFF);
        bytes[1] = (byte) ((userid >> 16) & 0xFF);
        bytes[2] = (byte) ((userid >> 8) & 0xFF);
        bytes[3] = (byte) ((userid >> 0) & 0xFF);
        bytes[4] = (byte) ((id >> 8) & 0xFF);
        bytes[5] = (byte) ((id >> 0) & 0xFF);
        return bytes;
    }

    public static byte[] longToByteArray(long longs) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) ((longs >> 56) & 0xFF);
        bytes[1] = (byte) ((longs >> 48) & 0xFF);
        bytes[2] = (byte) ((longs >> 40) & 0xFF);
        bytes[3] = (byte) ((longs >> 32) & 0xFF);
        bytes[4] = (byte) ((longs >> 24) & 0xFF);
        bytes[5] = (byte) ((longs >> 16) & 0xFF);
        bytes[6] = (byte) ((longs >> 8) & 0xFF);
        bytes[7] = (byte) ((longs >> 0) & 0xFF);
        return bytes;
    }


    /**
     * int到byte[] 由高位到低位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] shortToByteArray(short i) {
        byte[] result = new byte[2];
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }
    public static byte[] shortWriteToArray(byte[] bytes,short i) {
        bytes[0] = (byte) ((i >> 8) & 0xFF);
        bytes[1] = (byte) (i & 0xFF);
        return bytes;
    }

    public static short byteArrayToshort(byte[] bytes) {
        int value = 0;
        value= ((bytes[0]& 0xFF)<<8);
        value= (value+(bytes[1]& 0xFF));
        return (short) value;
    }
    public static short byteArrayToshort(byte[] bytes, int start) {
        int value = 0;
        value= ((bytes[0+start]& 0xFF)<<8);
        value= (value+(bytes[1+start]& 0xFF));
        return (short) value;
    }

    public static int byteArrayToInt(byte[] bytes, int start) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i+start] & 0xFF) << shift;
        }
        return value;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static long byteArrayToLong(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            int shift = (7 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
    public static boolean equals(byte[] bytesl,int l,byte[] bytes,int s,int start,int len) {
        if (bytesl==null || bytes==null )
            return false;
        for (int i=start; i<start+len; i++)
            if (bytesl[l+i] != bytes[s+i])
                return false;
        return true;
    }
    public static boolean equals(byte[] bytesl,byte[] bytes) {
        if (bytesl==null || bytes==null )
            return false;
        int length = bytes.length;
        for (int i=0; i<length; i++)
            if (bytesl[i] != bytes[i])
                return false;
        return true;
    }
    public static boolean equals(byte[] bytesl,byte[] bytes,int len) {
        for (; len>=0; len--)
            if (bytesl[len] != bytes[len])
                return false;
        return true;
    }

    public static boolean canBeEncodedStrict(byte[] bytes, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        String decoded = new String(bytes, charset);

        // 重新编码并比较原始字节
        byte[] reencoded = decoded.getBytes(charset);
        return java.util.Arrays.equals(bytes, reencoded);
    }

    public static void toFile(byte[] data, String name) throws IOException {//转化为图片
        String[] strings = name.split("\\.");
        String string = null;
        if (strings.length == 1) {
            String n = SHAutils.getMD5(name, false);
            string = XmlParser.cachepath + n + strings[0];
        } else {
            String n = SHAutils.getMD5(name, false);
            string = XmlParser.cachepath + n + "." + strings[strings.length - 1];
        }
        File file = new File(string);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {

            FileOutputStream fos = new FileOutputStream(file);
            //FileImageOutputStream fios =new FileImageOutputStream(file);
            fos.write(data, 0, data.length);
            fos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String primiteString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static class CreateName {
        public String create() {
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 16; i > 0; i--) {
                Random random = new Random();
                char s = primiteString.charAt(random.nextInt(62));
                stringBuilder.append(s);
            }
            return stringBuilder.toString();
        }
    }
    public static <K, V> V getRandomValue(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Map cannot be null or empty");
        }

        Collection<V> values = map.values();
        List<V> valuesList = new ArrayList<>(values);
        Random random = new Random();
        int randomIndex = random.nextInt(valuesList.size());

        return valuesList.get(randomIndex);
    }

    public static String getRandom(int len) {
        // 使用当前时间作为种子
        long seed = System.nanoTime();
        Random random = new Random(seed);
        // 生成随机字符串
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(primiteString.length());
            sb.append(primiteString.charAt(index));
        }
        return sb.toString();
    }


    public static class RandomPort {

        public int[] ints=new int[]{6000,8000,12000,23000,50000};
        public static ArrayList<Integer> ports=new ArrayList<>(50);
        public Integer create() {

            Random random = new Random();
            for (int p:ints){
                for (int i = 0; i < 10; i++) {
                    int a=p+i*i;
                    ports.add(a);
                }
            }
            int p=random.nextInt(49);
            return ports.get(p);
        }
    }

    public static InetAddress getBroadcastAddress(InetAddress ip, String subnetMask) throws UnknownHostException {
        byte[] ipBytes = ip.getAddress();
        byte[] maskBytes = InetAddress.getByName(subnetMask).getAddress();

        byte[] broadcastBytes = new byte[4];

        for (int i = 0; i < 4; i++) {
            // 对IP地址和子网掩码进行位或操作，得到广播地址
            broadcastBytes[i] = (byte) (ipBytes[i] | (maskBytes[i] ^ 0xFF));
        }

        // 将字节数组转换为InetAddress对象，再转换为String
        InetAddress broadcastAddr = InetAddress.getByAddress(broadcastBytes);
        return broadcastAddr;
    }
    public static String getSubnetMask(InetAddress address) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 跳过非活动的和回环的网络接口
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
            if (addresses.contains(address)) { // 假设我们只对IPv4感兴趣
                int[] networkPrefix = networkInterface.getInterfaceAddresses().stream()
                        .filter(ia -> ia.getAddress().equals(address))
                        .findFirst()
                        .map(InterfaceAddress::getNetworkPrefixLength)
                        .map(prefixLengthShort -> {
                            int prefixLength = (int) prefixLengthShort;
                            int[] mask = new int[4];
                            for (int i = prefixLength / 8; i > 0; i--) {
                                mask[i - 1] = 0xFF;
                            }
                            return mask;
                        })
                        .orElse(null);

                if (networkPrefix != null) {
                    // 将字节转换为点分十进制的字符串形式
                    StringBuilder sb = new StringBuilder();
                    for (int b : networkPrefix) {
                        if (sb.length() > 0) {
                            sb.append(".");
                        }

                        sb.append(String.format("%d", (b & 0xFF), 10));
                    }
                    System.out.println("IP Address: " + address.getHostAddress());
                    System.out.println("Subnet Mask: " + sb.toString());
                    return sb.toString();
                }
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSubnetMask() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 跳过非活动的和回环的网络接口
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (address instanceof Inet4Address) { // 假设我们只对IPv4感兴趣
                        int[] networkPrefix = networkInterface.getInterfaceAddresses().stream()
                                .filter(ia -> ia.getAddress().equals(address))
                                .findFirst()
                                .map(InterfaceAddress::getNetworkPrefixLength)
                                .map(prefixLengthShort -> {
                                    int prefixLength=(int)prefixLengthShort;
                                    int[] mask = new int[4];
                                    for (int i = prefixLength/8; i>0; i--) {
                                        mask[i-1]= 0xFF;
                                    }
//                                    byte[] mask = new byte[4];
//                                    for (int i = 0; i < mask.length; i++) {
////                                        int i1=(prefixLengthShort % 8);
////                                        int i2=(8 - i1) & 0xFF;
////                                        int i3=0xFF << i2;
////                                        mask[i] = (byte) (0xFF << (8 - (prefixLengthShort % 8)) & 0xFF);
//                                        mask[i] = (byte) (0xFF << (8 - (prefixLengthShort % 8)) & 0xFF);
//                                        if ((prefixLength -= 8) <= 0) {
//                                            break;
//                                        }
//                                    }
                                    return mask;
                                })
                                .orElse(null);


                        if (networkPrefix != null) {
                            // 将字节转换为点分十进制的字符串形式
                            StringBuilder sb = new StringBuilder();
                            for (int b : networkPrefix) {
                                if (sb.length() > 0) {
                                    sb.append(".");
                                }
//                                sb.append(String.format("%d", (b & 0xFF) + 0x100, 10).substring(1));
                                sb.append(String.format("%d", (b & 0xFF) , 10));
                            }

                            System.out.println("Interface: " + networkInterface.getName());
                            System.out.println("IP Address: " + address.getHostAddress());
                            System.out.println("Subnet Mask: " + sb.toString());
                            return sb.toString();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSize(800, 1200);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File("."));
        Frame farme = new Frame();
        farme.setSize(1000, 1010);
        int result = chooser.showOpenDialog(farme);
        String path = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getPath();
        }
        return path;
    }

    public static String chooseFilepath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSize(800, 1200);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setCurrentDirectory(new File("."));
        Frame farme = new Frame();
        farme.setSize(1000, 1010);
        int result = chooser.showOpenDialog(farme);
        String path = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getPath();
        }
        return path;
    }

    public static String sanc() {
        Scanner scanner = new Scanner(System.in);//从键盘接受数据
        String send = scanner.nextLine();//nextLine方式接受字符串
        return send;
    }

    public static boolean dealsSend(String username, byte[] bytes) {
        UserContext userContext = null;
        try {
            userContext = mainDataQueue.getUserContext1(username);
        } catch (Exception e) {
            System.out.println("dealsSend: null");
            return false;
        }
        byte[] b1 = getUseridByte(userContext.getBothId(), (short) 0);
        byte[] b = byteMerger(b1, bytes);
        DatagramPacket datagramPacket = new DatagramPacket(b, b.length, userContext.inetAddress, userContext.port);
        try {
            UDPclient.socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void linksDealsSend(String username, byte[] bytes) {
        DataQueue dataQueue = ReciveQueueFactory.getDataQueue(username);
        byte[] b = byteMerger(new byte[]{0}, bytes);
        DatagramPacket datagramPacket = new DatagramPacket(b, b.length, dataQueue.udpData.userRequest.toaddress, dataQueue.udpData.userRequest.toport);
        try {
            dataQueue.udpData.dataSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final byte[] JAR_MAGIC = {'P', 'K', 3, 4};

    public static boolean isJar(URL url) {
        return isJar(url, new byte[JAR_MAGIC.length]);
    }

    private static boolean isJar(URL url, byte[] buffer) {
        InputStream is = null;
        try {
            is = url.openStream();
            is.read(buffer, 0, JAR_MAGIC.length);
            if (Arrays.equals(buffer, JAR_MAGIC)) {//判断开始字节是不是jar包
                return true;
            }
        } catch (Exception e) {
            // Failure to read the stream means this is not a JAR
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        return false;
    }

    public static int getOs() {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.contains("win")) {
            return 1;
        } else if (name.contains("and")) {
            return 2;
        } else if (name.contains("lin")) {
            return 3;
        }
        return 0;
    }

    public static String removeFirstOccurrence(String originalString, String substringToRemove) {
        if (originalString == null || substringToRemove == null || substringToRemove.isEmpty()) {
            return originalString;
        }
        int index = originalString.indexOf(substringToRemove);
        if (index != -1) {
            return originalString.substring(0, index) + originalString.substring(index + substringToRemove.length());
        }
        return originalString;
    }
    public static String removeString(String originalString, String substringToRemove, int occurrenceCount) {
        if (originalString == null || substringToRemove == null || substringToRemove.isEmpty() || occurrenceCount <= 0) {
            return originalString;
        }
        int index = originalString.indexOf(substringToRemove);
        if (index == -1) {
            return originalString;
        }
        int currentCount = 1;
        while (currentCount < occurrenceCount) {
            index = originalString.indexOf(substringToRemove, index + substringToRemove.length());
            if (index == -1) {
                break;
            }
            currentCount++;
        }
        if (currentCount < occurrenceCount) {
            return originalString; // 没有找到指定次数的子字符串
        }
        return originalString.substring(0, index) + originalString.substring(index + substringToRemove.length());
    }

    public static void loadJar(String jarPath) {
        File jarFile = new File(jarPath);
        // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        }
        //获取方法的访问权限以便写回
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.setAccessible(accessible);
        }
    }

    public static class PathSort{
        public int i;
        public String path;
    }



    public static String pathSet(String s,String path){
        String strings=null;
        switch (s) {
            case "data":{strings="data&:"+path; break;}
            case "cloudefile":{strings="cloudefile&:"+path;break;}
            case "showpath":{strings="showpath&:"+path;break;}
            case "cache":{strings="cache&:"+path;break;}
//                case "data":{}
        }
        return strings;
    }

//":cloudefile&:"
    public static PathSort pathPrase(String path){
        String[] strings=null;
        try {
            path=URLDecoder.decode(path,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PathSort pathSort=new PathSort();

        String s=path.substring(0,1);
        int i=path.indexOf("&:");
        if (":".equals(s) && i!=-1){
            s=path.substring(1,i);
            strings=path.substring(1).split("&:");
            switch (s) {
                case "headPic":{
                    pathSort.i=1;
                    pathSort.path= XmlCreate.userShow+"/"+strings[1];
                    break;}
                case "data":{
                    pathSort.i=1;
                    pathSort.path= InitClass.absolute+strings[1];
                    break;}
                case "cloudefile":{
                    pathSort.i=2;
                    pathSort.path= XmlParser.cloudefile+strings[1];
                    break;}
                case "showpath":{
                    pathSort.i=3;
                    pathSort.path= XmlParser.showpath+strings[1];
                    break;}
                case "cache":{
                    pathSort.i=4;
                    pathSort.path= strings[1];
                    break;}
                case "cacheF":{
                    pathSort.i=5;
                    pathSort.path= XmlParser.cloudecache +"/cache_"+ SHAutils.getMD5(strings[1], true)+Tool.getPrex(strings[1]);
                    File file=new File(pathSort.path);
                    if (!file.exists()){
                        try {
                            long fileLong = file.length();
                            while (fileLong>578*128){
                                int ti=5;
                                File newFile=null;
                                if (ti<0){ break; }
                                ImageUtils.getImgObject().generateFixedSizeImage(strings[1],pathSort.path);
                                newFile=new File(pathSort.path);
                                strings[1]=pathSort.path;
                                ti--;
                                fileLong = newFile.length();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        FileOutputStream fileOutputStream;
                        FileInputStream fileFrom;
                        try {
                            fileOutputStream=new FileOutputStream(file);
                            fileFrom=new FileInputStream(new File(strings[1]));
                            fileFrom.getChannel().transferTo(0,fileFrom.getChannel().size(),fileOutputStream.getChannel());
                            fileOutputStream.close();
                            fileFrom.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case "cloudeCreate":{
                    pathSort.i=6;
                    File file=FileScan.createXmls(strings[1]);
                    pathSort.path=file.getAbsolutePath();
                    break;
                }
                case "FileDetail":{
                    pathSort.i=7;
                    File file=FileScan.createFileViewCacheXmls(strings[1]);
                    pathSort.path=file.getAbsolutePath();
                    break;
                }
                case "map":{
                    String map=JackJson.toJson(ProcessMapL.map);
                    String list=JackJson.toJson(ProcessMapL.list);
                    Map map1=new HashMap();
                    map1.put("map",map);
                    map1.put("list",list);
                    String data=JackJson.toJson(map1);
                    File file=new File(userlocal.username);
                    if (!file.exists()){
                        try {
                            file.createNewFile();
                            OutputStream outputStream=new FileOutputStream(file);
                            outputStream.write(data.getBytes());
                            pathSort.i=8;
                            pathSort.path=file.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
//                case "data":{}
            }
        }else {
            pathSort.i=0;
            pathSort.path= path;
        }
        return pathSort;
    }

    public static String encodeString(String original) {
        return Base64.getEncoder().encodeToString(original.getBytes());
    }

    public static String encodeBytes(byte[] original) {
        return Base64.getEncoder().encodeToString(original);
    }

    public static String getBitString(File file,int s,int length){
        byte[] bytes=new byte[length];
        try(FileInputStream input=new FileInputStream(file)){
            input.skip(s);
            input.read(bytes);
        }catch (Exception e){
            System.out.println("getBitString: "+e.getMessage());
        }
        int iMax = bytes.length - 1;
        if (iMax == -1){
            return "";
        }
        StringBuilder b = new StringBuilder();
        byte sc;
        for (int i = 0; i < bytes.length; i++) {
            sc=bytes[i];
            b.append(sc);
            if (sc >-100){
                if(sc<100){
                    if (sc<9 && sc>-1){
                        b.append(' ').append(' ');
                    }else {
                        if (sc>-10){
                            b.append(' ');
                        }
                    }
                }
                b.append(' ');
            }
            b.append(',');
        }
        return b.toString();
    }
    public static String getBitString1(File file,int s,int length){
        byte[] bytes=new byte[length];
        try(FileInputStream input=new FileInputStream(file)){
            input.skip(s);
            input.read(bytes);
        }catch (Exception e){
            System.out.println("getBitString: "+e.getMessage());
        }
        int iMax = bytes.length - 1;
        if (iMax == -1){
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            b.append(bytes[i]);
            b.append(',');
        }
        return b.toString();
    }

    public static byte[] hexStringToBytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        char[] chars = hex.toCharArray();
        for (int i = 0, j = 0; i < result.length; i++) {
            result[i] = (byte) (toByte(chars[j++]) << 4 | toByte(chars[j++]));
        }
        return result;
    }

    private static int toByte(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 0x0A);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 0x0a);
        throw new RuntimeException("invalid hex char '" + c + "'");
    }
    /**
     * 高效写法 byte数组转成16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0x0F];
            buf[c++] = digits[b & 0x0F];
        }
        return new String(buf);
    }

    private final static char[] digits = "0123456789ABCDEF".toCharArray();

    public static void unZip(String savepath,File file){
        try {
            ZipInputStream zis=new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry=zis.getNextEntry();
            while (zipEntry!=null){
                String path=savepath+File.separator+zipEntry.getName();
                if (!zipEntry.isDirectory()){
                    saveZip(zis,path);
                }else {
                    File dir=new File(path);
                    dir.mkdirs();
                }
                zis.closeEntry();
                zipEntry=zis.getNextEntry();
            }
            zis.close();

        } catch (Exception e) {

        }
    }

    public static Date convertToDate(int[] dateTimeArray,int offset) {
//        if (dateTimeArray == null || dateTimeArray.length != 6) {
//            throw new IllegalArgumentException("Invalid date time array");
//        }

        // 创建一个Calendar实例并设置日期时间值
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, dateTimeArray[0]);
        calendar.set(Calendar.MONTH, dateTimeArray[1] - 1); // 注意月份是从0开始的
        calendar.set(Calendar.DAY_OF_MONTH, dateTimeArray[2]);
        calendar.set(Calendar.HOUR_OF_DAY, dateTimeArray[3]);
        calendar.set(Calendar.MINUTE, dateTimeArray[4]);
        calendar.set(Calendar.SECOND, dateTimeArray[5]);
        calendar.set(Calendar.MILLISECOND, 0); // 如果需要的话，可以设置毫秒

        // 转换为Date对象
        return calendar.getTime();
    }

    public static LocalDateTime convertToDateTime(String[] dateTimeStringArray,int offest) {
        if (dateTimeStringArray == null || dateTimeStringArray.length-offest != 6) {
            throw new IllegalArgumentException("Invalid date time array");
        }

        int year = Integer.parseInt(dateTimeStringArray[offest+0]);
        int month = Integer.parseInt(dateTimeStringArray[offest+1]) ; // 月份从1开始，但数组中的月份是从1开始的，所以需要-1
        int day = Integer.parseInt(dateTimeStringArray[offest+2]);
        int hour = Integer.parseInt(dateTimeStringArray[offest+3]);
        int minute = Integer.parseInt(dateTimeStringArray[offest+4]);
        int second = Integer.parseInt(dateTimeStringArray[offest+5]);

        // 创建LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        return dateTime;
    }

    // 获取当前日期的字符串数组
    public static String[] getCurrentDateStringArray() {
        LocalDateTime now = LocalDateTime.now();
        return new String[]{
                String.valueOf(now.getYear()),
                String.valueOf(now.getMonthValue()),
                String.valueOf(now.getDayOfMonth()),
                String.valueOf(now.getHour()),
                String.valueOf(now.getMinute()),
                String.valueOf(now.getSecond())
        };
    }


    public static void unZip(String savepath,InputStream file){
        try {
            ZipInputStream zis=new ZipInputStream(file);
            ZipEntry zipEntry=zis.getNextEntry();
            while (zipEntry!=null){
                String path=savepath+File.separator+zipEntry.getName();
                if (!zipEntry.isDirectory()){
                    saveZip(zis,path);
                }else {
                    File dir=new File(path);
                    dir.mkdirs();
                }
                zis.closeEntry();
                zipEntry=zis.getNextEntry();
            }
            zis.close();

        } catch (Exception e) {

        }
    }
    public static void saveZip(ZipInputStream zipInputStream,String path) throws IOException {
        BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(path));
        byte[] bytes=new byte[4096];
        int read;
        while ((read=zipInputStream.read(bytes))!=-1){
            bos.write(bytes,0,read);
        }
        bos.flush();
        bos.close();

    }


}