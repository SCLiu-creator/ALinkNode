package superlink.udpbind.client.recives.datalen.dataReq;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.util.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class dataInteger  {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
//    public int fp=1446;
    public int fp=1442;

    DataReqAuto dataReqAuto;
    public dataInteger(DataReqAuto dataReqAuto){
        System.out.println("revor");
        this.userContext= dataReqAuto.userContext;
        this.id= dataReqAuto.id;
        blockingQueue= dataReqAuto.blockingQueue;
        this.senders= dataReqAuto.senders;
        this.dataReqAuto = dataReqAuto;
    }

    public byte[] reqFile(long timeLong){
        long time= timeLong;
        int i=0;
        int l;
        byte[][] bytess=null;
        int i1=0;
        long ot=time;
        fp = dataReqAuto.sdr.pl-8;
        if (dataReqAuto.sdr.page > 0){
            int page=dataReqAuto.sdr.page;//int page=dataReqAuto.sdr.page-Integer.MAX_VALUE;
            bytess=new byte[dataReqAuto.sdr.page][];
            dataReqAuto.data=bytess;
            Integer pos=0;//Integer pos=Integer.MIN_VALUE;
            int j=0;
            int t=0;
            int index=0;
            blockingQueue.clear();
            System.out.println("page:  "+page );
            byte[] bytes=null;
            long l1 = 10;
            long time0=0;
            while (true){
                i=0;
                pos=0;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b==null){
                        i++;
                        senders.send(Utils.intToByteArray(pos));
                    }
                    if (i == 255){
                        break;
                    }
                    pos++;
                }
                if (i==0){
                    break;
                }

                l1=(long) (time*Math.log1p(i));
                time0=System.currentTimeMillis();
//                    l1= (long) ((long) Math.log(time)*Math.log(258-i)*20);
//                    Thread.sleep(l1/2 );
                t=0;
                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        try {
                            l1=l1-(System.currentTimeMillis()-time0);
                            if (l1>0){
                                bytes=blockingQueue.poll(l1,TimeUnit.MILLISECONDS);
                            }else {
                                time0=System.currentTimeMillis()-time0;
                                if (time0<2){
                                    time0=2;
                                }
                                time= (long) (time0/Math.log1p(i));
                                break;
                            }
//                            senders.send(Utils.intToByteArray(pos-1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bytes==null){
                            continue;
                        }
                    }else {
                        l1=timeLong;
                    }
                    try {
                        index=Utils.byteArrayToInt(bytes);
                        j=0;
                        t++;
                        if (bytess[index]!=null){
                            continue;
                        }
                        l= (int) Utils.calculateChecksum(bytes,0,bytes.length-4);
                        i1=Utils.byteArrayToInt(bytes,bytes.length-4);
                        if(!Objects.equals(l,i1)){
                            System.out.println("Ehe: "+l);
                            System.out.println("Ehei: "+index);
                            continue;
                        }
//                        System.out.println("Che: "+l);
//                        System.out.println("p: "+i1);
                        bytess[index]= Utils.subByte(bytes,4,bytes.length-8);
                        System.out.println("revoer+"+index);
//                        to++;
//                        System.out.println(to);
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println(Arrays.toString(bytes));
                        System.out.println(new String(bytes));
                    }
                }
                dataReqAuto.cj=j;
                if (j>=3){
                    UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null){
                        break;
                    }else {
                        if (j>10){
                            break;
                        }
                        if (j%3==0){
                            if (userContext.getQueue(id)!=this.blockingQueue){
                                break;
                            }else {
                                this.userContext=userContext;
                                this.senders.InitInit(this.id,userContext);
                            }
                        }
                    }
                }
                if (t==0){
                    if (time<2048){
                        time=time*2;
                    }
                    if (time<0){
                        time=timeLong;
                    }
                    if (time==0){
                        time=2;
                    }
                }else {
                    if(time>8){
                        time=time/2;
                    }
                }
                if (time>2048){
                    time=2048;
                }
                j++;
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        }else {
            return new byte[0];
        }
        int length=bytess.length-1;
        byte[] bytesss=bytess[length];
        l=bytesss.length;

//        int cheak=0;
//        int ach=0;
//        for (int j = 0; j < bytessText.length; j++) {
//            ach= (int) Utils.calculateChecksum(bytessText[j],0,bytessText[j].length-4);
//
//            cheak=Utils.byteArrayToInt(bytessText[j],bytessText[j].length-4);
//
//            i1=Utils.byteArrayToInt(Utils.subByte(bytessText[j],0,4));
//            if (bytess[j]!=null){
//                int a=Arrays.hashCode(bytess[j]);
//                int b=Arrays.hashCode(Utils.subByte(bytessText[j],4,bytessText[j].length-8));
//                if (a!=b){
//                    System.out.println(ach+" "+cheak+" "+i1);
//                }
//            }
//        }

        l=((dataReqAuto.sdr.page-1)*fp)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*fp,bytes.length);
            i++;
//            System.out.println(Arrays.toString(bytes));
        }

        System.out.println(i);
        System.out.println("over");
        return rev;
    }

    public void sends(){
        fp = dataReqAuto.sdr.pl-8;
        File file=new File(dataReqAuto.sdr.filename);
        long fl=file.length();
        if (file.exists()){
            if (fl<Integer.MAX_VALUE){
                dataReqAuto.sdr.page= Math.toIntExact(fl / fp);
                if ((fl %fp)!=0){dataReqAuto.sdr.page+=1;}
            }
        }else {
            dataReqAuto.sdr.page=0;
            String send="DI"+JSON.toJSONString(dataReqAuto.sdr);
            senders.send(send.getBytes());
            senders.send(send.getBytes());
            return;
        }
        byte[] send=("DI"+JSON.toJSONString(dataReqAuto.sdr)).getBytes();
        senders.send(send);
        byte[] bytes=new byte[fp];
        byte[] bsend=null;
        int p=0;
        int len=0;
        RandomAccessFile randomFile=null;
        try {
             randomFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        byte[][] cache=new byte[dataReqAuto.sdr.page][];
        byte[] re=null;
        String s = "";
        byte[] pre=null;
        int j=0;

        long l=0;
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        while (true){
            //System.out.println("sending");
            try {
                re=blockingQueue.poll(4,TimeUnit.SECONDS);
                if (re.length!=4){
                    s=new String(re,0,2);
                    if ("OK".equals(s) && re.length==2){
                        break;
                    }
                }
            } catch (Exception e) {
                senders.send(send);
                System.out.println(e.getMessage());
                if (re==null){
                    if (j>3){
                        UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                        if (userContext==null){
                            break;
                        }else {
                            if (userContext.getQueue(id)==null){
                                break;
                            }
                            if (j%3==0){
                                this.userContext=userContext;
                                this.senders.InitInit(this.id,userContext);
                            }
                            if (j>12){
                                break;
                            }
                        }
                    }
                    j++;
                }
                continue;
            }

            try {
                pre=Utils.subByte(re,0,4);
                p=Utils.byteArrayToInt(re);
                if (cache[p]!=null){
                    senders.send(cache[p]);
                    continue;
                }
                System.out.println("sending "+Utils.byteArrayToInt(pre));

                randomFile.seek(p*fp);
                len=randomFile.read(bytes);

                if (len!=fp){
                    bsend=Utils.subByte(bytes,0,len);
                }else {
                    bsend=bytes;
                }
                send=Utils.byteMerger(pre,bsend);
                l=Utils.calculateChecksum(send,0,send.length);
                pre=Utils.intToByteArray((int) l);
                send=Utils.byteMerger(send,pre);
                senders.send(send);
                cache[p]=send;
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            j=0;
        }
//        String fn=dataReqAuto.sdr.filename.replace(".png","_1.png");
//        String fn2=dataReqAuto.sdr.filename.replace(".png","_2.png");
//        int length=cache.length-1;
//        byte[] bytesss=new byte[(cache.length-1)*fp+length];
//        l=bytesss.length;
//
//        int i=0;
//        try{
//            File file1=new File(fn);
//            FileOutputStream inputStream1=new FileOutputStream(file1);
//            File file2=new File(fn2);
//            FileOutputStream inputStream2=new FileOutputStream(file2);
//            for (byte[] bytess:cache){
//                System.arraycopy(bytess,4,bytesss,i*fp,bytess.length-8);
//                inputStream2.write(bytesss,4,bytess.length-4);
//                i++;
////            System.out.println(Arrays.toString(bytes));
//            }
//            inputStream1.write(bytesss);
//
//        }catch (Exception e){
//            e.getMessage();
//        }
        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("over");
    }

    @Override
    public int hashCode(){
        return dataReqAuto.sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    public static void main(String[] args) throws IOException {
        byte[] bytestt= Files.readAllBytes(new File("E:\\udpclient\\7211.jpg").toPath());
        byte[] bt0=Utils.subByte(bytestt,0,1440);
        byte[] bt1;
        for (int i=1440;i<bytestt.length;i=i+1440){
            bt1=Utils.subByte(bytestt,i,i+1440);
            if(Arrays.equals(bt0,bt1)){
                System.out.println(i);
            }
            bt0=bt1;
        }
        checkForDuplicateSegments(bytestt,1440);

        int i0=1231223232;
        int i1=1231223230;
        File file=new File("D:\\java\\udpclient\\data\\cloudecache\\cache_4A4B4DA130E5EFF438173501C7778E08.jpg");
        if (file.exists()){


        }
        long B=1449*256;
        if (file.length()<B) {
            System.out.println();
        }
        System.out.println(file.length());
        if (i0==i1+2){
            System.out.println("");
        }
        Object o=null;
        System.out.println("aaa"+o);
        byte[] bytes0= Files.readAllBytes(new File("E:\\udpclient\\t0.jpg").toPath());
        byte[] bytes1= Files.readAllBytes(new File("E:\\udpclient\\t1.jpg").toPath());

        int t=hasDuplicateSegments(bytes0,1442);
        int t2=hasDuplicateSegments(bytes1,1442);

        int pos=0;
        boolean p=false;
        long l1=0;
        long l2=0;
        for (int i = 0; i <bytes1.length ; i=i+1442) {
            for (int j = 1; j <1442 ; j++) {
                if (bytes1[i+j] != bytes0[i+j]){
                    l1= Utils.calculateChecksum(bytes0,i,1442);
                    l2= Utils.calculateChecksum(bytes1,i,1442);
                    if (p){
                    }else {
                        System.out.println(i/1442+"  "+j);
                    }
                }
                pos=i;
            }
        }
    }
    public static boolean compareByteArraysInChunks(byte[] a, byte[] b, int chunkSize) {
        // 计算每个数组可以分成多少个段
        int numChunksA = (a.length + chunkSize - 1) / chunkSize;
        int numChunksB = (b.length + chunkSize - 1) / chunkSize;

        // 如果段数不同，直接返回false
        if (numChunksA != numChunksB) {
            return false;
        }

        // 逐一比较每一段
        for (int i = 0; i < numChunksA; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, a.length);

            // 获取a和b数组的当前段
            byte[] chunkA = new byte[end - start];
            byte[] chunkB = new byte[end - start];

            System.arraycopy(a, start, chunkA, 0, chunkA.length);
            System.arraycopy(b, start, chunkB, 0, chunkB.length);

            // 比较当前段
            if (!Arrays.equals(chunkA, chunkB)) {
                return false;
            }
        }
        return true;
    }
        public static int hasDuplicateSegments(byte[] array, int segmentLength) {
            // 用于存储已经遇到的段的集合
            Set<String> seenSegments = new HashSet<>();

            // 确定数组可以分成的段数
            int numSegments = (int) Math.ceil((double) array.length / segmentLength);

            // 遍历每个段
            for (int i = 0; i < numSegments; i++) {
                int start = i * segmentLength;
                int end = Math.min(start + segmentLength, array.length);

                // 创建当前段的副本（因为byte数组是引用类型，我们需要一个新的数组实例）
                byte[] segment = Arrays.copyOfRange(array, start, end);

                // 将段转换为字符串（或使用其他哈希方法），以便存储在HashSet中
                // 注意：这种方法可能不是最高效的，特别是对于大段数据，因为它涉及到数组复制和字符串转换
                // 但对于小段数据（如1442字节），这通常是可行的
                String segmentString = Arrays.toString(segment);

                // 检查段是否已经在集合中
                if (!seenSegments.add(segmentString)) {
                    // 如果add方法返回false，说明集合中已经存在该段
                    return i;
                }
            }

            // 没有找到重复段
            return 0;
        }

    public static void checkForDuplicateSegments(byte[] array, int n) {
        if (array == null || n <= 0 || n > array.length) {
            System.out.println("Invalid input parameters.");
            return;
        }

        Set<String> segments = new HashSet<>();
        List<Integer> duplicatePositions = new ArrayList<>();

        for (int i = 0; i <= array.length - n; i++) {
            // 创建当前段的字符串表示
            StringBuilder segmentBuilder = new StringBuilder();
            for (int j = i; j < i + n; j++) {
                segmentBuilder.append(array[j]);
                if (j < i + n - 1) {
                    segmentBuilder.append(","); // 添加分隔符，以便区分不同段的相同数字
                }
            }
            String segment = segmentBuilder.toString();

            // 检查当前段是否已经存在于集合中
            if (segments.contains(segment)) {
                // 如果是重复的段，记录位置
                duplicatePositions.add(i);
            } else {
                // 如果不是重复的段，添加到集合中
                segments.add(segment);
            }
        }

        // 打印重复段的位置
        if (!duplicatePositions.isEmpty()) {
            System.out.println("Duplicate segments found at positions:");
            for (int position : duplicatePositions) {
                System.out.println(position);
            }
        } else {
            System.out.println("No duplicate segments found.");
        }
    }

//    public static void checkForDuplicateSegments(int[] array, int n) {
//        if (array == null || n <= 0 || n > array.length) {
//            System.out.println("Invalid input parameters.");
//            return;
//        }
//
//        Set<String> segments = new HashSet<>();
//
//        for (int i = 0; i <= array.length - n; i++) {
//            // 创建当前段的字符串表示
//            StringBuilder segmentBuilder = new StringBuilder();
//            for (int j = i; j < i + n; j++) {
//                segmentBuilder.append(array[j]);
//            }
//            String segment = segmentBuilder.toString();
//
//            // 检查当前段是否已经存在于集合中
//            if (segments.contains(segment)) {
//                // 如果是重复的段，打印位置
//                System.out.println("Duplicate segments found at positions: " + i + " and " + (findPreviousSegmentPosition(segments, segment, i, n)));
//                return; // 找到一对重复的段后即可返回
//            } else {
//                // 如果不是重复的段，添加到集合中
//                segments.add(segment);
//            }
//        }
//
//        System.out.println("No duplicate segments found.");
//    }
//
//    // 辅助方法，用于找到之前相同段的起始位置
//    private static int findPreviousSegmentPosition(Set<String> segments, String segment, int currentPos, int n) {
//        for (int i = currentPos - n; i >= 0; i -= n) {
//            StringBuilder previousSegmentBuilder = new StringBuilder();
//            for (int j = i; j < i + n; j++) {
//                previousSegmentBuilder.append(j < array.length ? array[j] : 0); // 确保不会越界
//            }
//            if (previousSegmentBuilder.toString().equals(segment)) {
//                return i;
//            }
//        }
//        return -1; // 如果没有找到，返回-1
//    }

}
