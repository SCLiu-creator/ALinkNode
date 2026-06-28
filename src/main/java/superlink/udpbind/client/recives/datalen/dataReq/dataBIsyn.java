package superlink.udpbind.client.recives.datalen.dataReq;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.SHAutils;
import superlink.util.Utils;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class dataBIsyn {
    public UserContext userContext;
    public int id;
    Senders senders;
    ByteBufer blockingQueue;
    DataRequest sdr;

    public dataBIsyn(DataReqAuto dataReqAuto) {
        System.out.println("revor");
        this.userContext = dataReqAuto.userContext;
        this.id = dataReqAuto.id;
        blockingQueue = dataReqAuto.blockingQueue;
        this.senders = dataReqAuto.senders;
    }

    int pagelength = 1445 * 256;

    public File reqFile(DataRequest dataRequest, Long time) {
        int page = (int) (dataRequest.l / pagelength);
        int remain = Math.toIntExact(dataRequest.l % pagelength);
        if (remain>0){page++;}
        time = (long) Math.log(time * page) * 50;
        boolean[] booleans = new boolean[page];
        File file = new File(XmlParser.cloudecache + SHAutils.getMD5(dataRequest.filename, true));
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

        System.out.println("page total: "+page);

        int sy = 0;
        while (sy < page) {
            sy=0;
            int pag = 0;
            byte[] bytes;
            for (boolean b : booleans) {
                if (b == false) {
                    bytes = reqbyte(pag * pagelength, time);
                    blockingQueue.clear();
                    try {
                        fileOutputStream.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
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

    public void sends(DataRequest sdr) {

        this.sdr = sdr;
        File file = new File(sdr.filename);
        if (file.exists()) {
            sdr.l = file.length();
        }
        String send = "BI" + JSON.toJSONString(sdr);
        senders.send(send.getBytes());
        System.out.println(sdr.page);

        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BlockingQueue<filebodysyn> linkedQueue=new LinkedBlockingQueue(10);
        try {
            new Thread(new BIBuffer(linkedQueue,randomFile)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataBI.filebody fileb = null;
        int c = 0;
        byte[] data = new byte[pagelength];
        byte[] bytes = null;
        int len = 0;
        String s = null;
        filebodysyn filebodysyn=null;
        while (true) {

            try {
                bytes = blockingQueue.poll(2, TimeUnit.SECONDS);
                s = new String(bytes, 0, 2);
            } catch (Exception e) {
                e.printStackTrace();
                c++;
                if (c >= 3) {
                    break;
                }
                continue;
            }
            if ("rb".equals(s)) {
                String s1 = new String(bytes, 2, bytes.length - 2);
                fileb = JSON.parseObject(s1, dataBI.filebody.class);
                try {
                    filebodysyn=linkedQueue.poll(6,TimeUnit.SECONDS);
                    data=filebodysyn.bytes;
                    fileb.pos=filebodysyn.pos;
                    fileb.l=filebodysyn.l;
                    fileb.hash=filebodysyn.hash;

                } catch (InterruptedException e) {
                    try {
                        randomFile.seek(fileb.pos);
                        len = randomFile.read(bytes, 0, pagelength);
                    } catch (IOException ie) {
                    }
                    if (data.length!= len){
                        data = Utils.subByte(bytes, 0, pagelength);
                    }

                }
                sendbyte(data,len ,fileb);
                c = 0;
            }
            if ("OK".equals(s) || c >= 3) {
                break;
            }
        }
        linkedQueue=null;
        System.out.println("OVER");
    }


    public byte[] reqbyte(long pos, Long time) {
        int i = 0;
        int l;
        dataBI.filebody file = new dataBI.filebody();
        file.pos = pos;
        String send = "rb" + JSON.toJSONString(file);
        senders.send(send.getBytes());
        int c = 0;
        while (true) {
            String s = null;
            byte[] bytes = null;
            try {
                bytes = blockingQueue.poll(time*3, TimeUnit.MILLISECONDS);
                s = new String(bytes, 0, 2);
                file = JSON.parseObject(new String(bytes, 2, bytes.length - 2), dataBI.filebody.class);
            } catch (Exception e) {
                e.printStackTrace();
                senders.send(send.getBytes());
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
        l = file.l / 1445;
        if (file.l % 1445 > 0) {
            l += 1;
        }

        byte[][] bytess = null;
        int j = 0;
        bytess = new byte[l][];
        int pos1 = 0;
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
                    if (pos1>=l){break;}
                    try {
                        bytes=blockingQueue.poll(time,TimeUnit.MILLISECONDS);
                        if (bytes==null){
                            j++;
                            if (j >2) {
                                break;
                            }}
                    } catch (InterruptedException e) {
                        j++;
                        if (j >2) {
                            break;
                        }
                    }
                }else {
                    bytess[bytes[0] + 128] = Utils.subByte(bytes, 1, bytes.length - 1);
                    j=0;
                    pos1++;
                }

            }

            if (j == 4) {
                System.out.println("error rev");
                return null;
            }
        }
        senders.send("ok".getBytes());
        senders.send("ok".getBytes());
        int len = bytess[l - 1].length;
        l = ((l - 1) * 1445) + len;
        byte[] rev = new byte[l];
        i = 0;
        for (byte[] bytes : bytess) {
            System.arraycopy(bytes, 0, rev, i * 1445, bytes.length);
            i++;
        }
        String hash=SHAutils.getShaFromByte(rev, SHAutils.MD_5, false);
        if (!file.hash.equals(hash)) {
            System.out.println("join Recursive call pos: "+file.pos);
            System.out.println("join Recursive call hash: "+hash);
            return reqbyte(file.pos, time);
        }
        return rev;
    }

    public void sendbyte(byte[] buffer,int len, dataBI.filebody file) {
        file.l = len;
        file.hash = SHAutils.getShaFromByte(buffer, SHAutils.MD_5, false);
        String send = "br" + JSON.toJSONString(file);
        senders.send(send.getBytes());
        byte[] bytes;
        int p;
//        int len = 0;
        byte[] re;
        String s = "";
        while (true) {
            try {
                re = blockingQueue.poll(2, TimeUnit.SECONDS);
                s = new String(re);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if ("ok".equals(s)) {
                blockingQueue.clear();
                break;
            } else {
//                System.out.println("sending b: " + re[0]);

                p = (re[0] + 128) * 1445;
                bytes = Utils.subByte(buffer, p, 1445);
//                if (bytes.length != 1445) {
//                    bytes = Utils.subByte(bytes, 0, len);
//                }

                senders.send(Utils.byteMerger(new byte[]{re[0]}, bytes));
//                System.out.println(Arrays.toString(bytes));
            }
        }
        System.out.println("over");
    }

    public static class filebodysyn {
        public long pos;
        public int l;
        public String hash;
        public byte[] bytes;
    }
    public class BIBuffer implements Runnable{

        BlockingQueue blockingQueue;
        RandomAccessFile randomFile;
        int pos=0;
        long length;
        public BIBuffer(BlockingQueue blockingQueue,RandomAccessFile randomAccessFile) throws IOException {
            this.randomFile=randomAccessFile;
            this.blockingQueue=blockingQueue;
            try {
                length=randomAccessFile.length();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] bytes=new byte[pagelength];
            int len=0;
            while (pos<len) {
                filebodysyn filebodysyn=new filebodysyn();
                try {
                    randomFile.seek(pos);
                    len = randomFile.read(bytes, 0, pagelength);

                    //System.out.println(Arrays.toString(data));
                } catch (IOException e) {

                }

                byte[] data = Utils.subByte(bytes, 0, pagelength);
                filebodysyn.l = Math.toIntExact(len);
                filebodysyn.pos=pos;
                filebodysyn.hash = SHAutils.getShaFromByte(data, SHAutils.MD_5, false);
                filebodysyn.bytes=data;
                try {
                    blockingQueue.put(filebodysyn);
                } catch (InterruptedException e) {
                }
                pos=pagelength+pos;
            }


        }
    }


    @Override
    public int hashCode() {
        return sdr.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode() ? true : false;
    }

}
