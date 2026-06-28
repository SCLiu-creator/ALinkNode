package superlink.udpbind.client.recives.datalen.dataReq;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.SHAutils;
import superlink.util.Utils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class dataBI {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    DataReqAuto dataReqAuto;
    public int fp=1442;

    public dataBI(DataReqAuto dataReqAuto) {
        System.out.println("revor");
        this.userContext = dataReqAuto.userContext;
        this.id = dataReqAuto.id;
        blockingQueue = dataReqAuto.blockingQueue;
        this.senders = dataReqAuto.senders;
        this.dataReqAuto = dataReqAuto;
    }

    public File reqFile(Long time) {
        fp = dataReqAuto.sdr.pl - 8;
        int pagelength = fp * 256;
        int page = (int) (dataReqAuto.sdr.l / pagelength);
        int remain = Math.toIntExact(dataReqAuto.sdr.l % pagelength);
        if (remain>0){page++;}
        time = (long) Math.log(time * page) * 50;
        boolean[] booleans = new boolean[page];
        File file = new File(XmlParser.cloudecache + SHAutils.getMD5(dataReqAuto.sdr.filename, true));
        FileOutputStream fileOutputStream = null;

        if (file.exists()){
            try {
                fileOutputStream = new FileOutputStream(file);
                long filelength=file.length();
                int fp= Math.toIntExact(filelength / pagelength);
                int i=0;
                for (boolean b:booleans){
                    if (fp>i){
                        booleans[i]=true;
                    }
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else {
            try {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        dataReqAuto.data=file;
        System.out.println("page total: "+page);

        int sy = 0;
        while (sy < page) {
            sy=0;
            int pag = 0;
            byte[] bytes;
            for (boolean b : booleans) {
                if (b == false) {
                    bytes = reqbyte(pag * pagelength, time,fp);
                    blockingQueue.clear();
                    try {
                        fileOutputStream.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //continue;
                    }
                    booleans[pag] = true;
                } else {
                    sy++;
                }
                pag++;
            }
//            bytes = reqbyte(pag * pagelength, time);
//            blockingQueue.clear();
            System.out.println("page: "+pag);
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        senders.send("OK".getBytes());
        senders.send("OK".getBytes());

        return file;
    }

    public void sends() {
        fp = dataReqAuto.sdr.pl-8;
        int pagelength = fp * 256;
        File file = new File(dataReqAuto.sdr.filename);
        if (file.exists()) {
            dataReqAuto.sdr.l = file.length();
        }else {
            dataReqAuto.sdr.l=0;
            String send="BI"+JSON.toJSONString(dataReqAuto.sdr);
            senders.send(send.getBytes());
            senders.send(send.getBytes());
            return;
        }
        String send = "BI" + JSON.toJSONString(dataReqAuto.sdr);
        senders.send(send.getBytes());
        System.out.println(dataReqAuto.sdr.page);

        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        filebody fileb = null;
        int c = 0;
        while (true) {
            String s = null;
            int len = 0;
            byte[] bytes = null;
            byte[] data = new byte[pagelength];

            try {
                bytes = blockingQueue.poll(2, TimeUnit.SECONDS);
                s = new String(bytes, 0, 2);
            } catch (Exception e) {
                e.printStackTrace();
                c++;
                if (c >= 3) {
                    UserContext userContext= UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null ){
                        break;
                    }else {
                        if (userContext.getQueue(id)!=blockingQueue){
                            break;
                        }
                        if (c%5==0){
                            this.userContext=userContext;
                            this.senders.InitInit(this.id,userContext);
                        }

                    }
                }
                continue;
            }
            if ("rb".equals(s)) {
                String s1 = new String(bytes, 2, bytes.length - 2);

                try {
                    fileb = JSON.parseObject(s1, filebody.class);
                    randomFile.seek(fileb.pos);
                    len = randomFile.read(data, 0, pagelength);
                    //System.out.println(Arrays.toString(data));
                } catch (Exception e) {
                    continue;
                }
                if (len != pagelength) {
                    data = Utils.subByte(data, 0, len);
                }
                System.out.println("sending p: " + fileb.pos);
                sendbyte(data,len ,fileb,fp);
                c = 0;
            }
            if ("OK".equals(s) || c >= 3) {
                break;
            }
        }
        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("OVER");
    }


    public byte[] reqbyte(long pos, Long time,int pl) {
        int i = 0;
        int l;
        filebody file = new filebody();
        file.pos = pos;
        String send = "rb" + JSON.toJSONString(file);
        senders.send(send.getBytes());
        int c = 0;
        long time0=time;
        while (true) {
            String s = null;
            byte[] bytes = null;
            try {
                bytes = blockingQueue.poll(time*3, TimeUnit.MILLISECONDS);
                s = new String(bytes, 0, 2);
                file = JSON.parseObject(new String(bytes, 2, bytes.length - 2), filebody.class);
            } catch (Exception e) {
                e.printStackTrace();
                senders.send(send.getBytes());
                time=time*2;
                c++;
                if (c > 5) {
                    break;
                }
            }
            if ("br".equals(s)) {
                System.out.println("filehash: "+file.hash);
                break;
            }
        }
        l = file.l / pl;
        if (file.l % pl > 0) {
            l += 1;
        }

        byte[][] bytess = null;
        int j = 0;
        bytess = new byte[l][];
        int pos1 = 0;
        time=time0;
        while (true) {
            i = 0;
            pos = Byte.MIN_VALUE;
            //System.out.println("revcheak");
            for (byte[] b : bytess) {
                if (b != null) {
                    i++;
                } else {
                    senders.send(new byte[]{(byte) pos, 0});
                }
                pos++;
            }
            if (i == l) {
                break;
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] bytes = null;
            while (true) {
                bytes = blockingQueue.poll();
                if (bytes == null) {
//                    if (pos1>=l){
//                        break;
//                    }
                    try {
                        bytes=blockingQueue.poll(time,TimeUnit.MILLISECONDS);
                        time=time*2;
                        if (bytes==null){
                            if (j >3) {
                                break;
                            }
                            j++;
                        }
                    } catch (Exception e) {
                        j++;
                    }
                }else {
                    bytess[bytes[0] + 128] = Utils.subByte(bytes, 1, bytes.length - 1);
                    j=0;
                    //pos1++;
                }
            }

            if (j == 7) {
                System.out.println("error rev");
                return null;
            }
        }
        senders.send("ok".getBytes());
        senders.send("ok".getBytes());
        int len = bytess[l - 1].length;
        l = ((l - 1) * pl) + len;
        byte[] rev = new byte[l];
        i = 0;
        for (byte[] bytes : bytess) {
            System.arraycopy(bytes, 0, rev, i * pl, bytes.length);
            i++;
        }
        String hash=SHAutils.getShaFromByte(rev, SHAutils.MD_5, false);
        if (!file.hash.equals(hash)) {
            System.out.println("join Recursive call pos: "+file.pos);
            System.out.println("join Recursive call hash: "+hash);
            return reqbyte(file.pos, time,fp);
        }
        return rev;
    }

    public void sendbyte(byte[] buffer,int len, filebody file,int pl) {
        file.l = len;
        file.hash = SHAutils.getShaFromByte(buffer, SHAutils.MD_5, false);
        String send = "br" + JSON.toJSONString(file);
        senders.send(send.getBytes());
        byte[] bytes;
        int p;
//        int len = 0;
        byte[] re = null;
        String s = "";
        int c=0;
        while (true) {
            try {
                re = blockingQueue.poll(2, TimeUnit.SECONDS);
                if (re.length!=1){
                    s = new String(re);
                    if ("ok".equals(s)) {
                        blockingQueue.clear();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (c>4){
                    UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null){
                        break;
                    }else {
                        if (c%4==0){
                            this.userContext=userContext;
                            this.senders.InitInit(this.id,userContext);
                        }
                    }
                    if (c>7){
                        break;
                    }
                }

            }
//                System.out.println("sending b: " + re[0]);
            p = (re[0] + 128) * pl;
            bytes = Utils.subByte(buffer, p, pl);
            senders.send(Utils.byteMerger(new byte[]{re[0]}, bytes));
//                System.out.println(Arrays.toString(bytes));
        }
        System.out.println("over");
    }

    public static class filebody {
        public long pos;
        public int l;
        public String hash;
    }


    @Override
    public int hashCode() {
        return dataReqAuto.sdr.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode() ? true : false;
    }

}
