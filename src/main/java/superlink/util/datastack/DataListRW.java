package superlink.util.datastack;

import superlink.filemanage.xmltool.XmlParser;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import static superlink.util.Utils.*;

public class DataListRW {

    int pos;

    RandomAccessFile randomAccessFile;
    UB ub;
    public HashMap<String,UB> hashMap=new HashMap<>();
    public static int datastar;
    public static int userLen=16;
    public static String defPath= XmlParser.dir+"permiss";
    public static LinkedList<UB> emptyList=new LinkedList();
    public DataListRW() throws Exception {
        this.pos=0;
        if (new File(defPath).exists()){
            this.randomAccessFile=new RandomAccessFile(defPath,"rw");
        }else {
            this.randomAccessFile=newFile();
        }
    }
    public DataListRW selectUser(String user) throws Exception {
        byte[] bytes=new byte[userLen+4];
        byte[] byte0=new byte[4];
        randomAccessFile.seek(0);
        randomAccessFile.read(byte0);
        int len0=byteArrayToInt(byte0);
        DataListRW.datastar=len0;
        int pos=4;
        long filelen=randomAccessFile.length();
        randomAccessFile.seek(pos);
        String s="";
        int userstar=0;
        while (!s.equals(user)){
            if (pos>filelen){
                throw new Exception("no find user");
            }
            randomAccessFile.read(bytes);
            s=new String(bytes,0,userLen);
            userstar=byteArrayToInt(bytes,userLen);
            if (s.equals("") || userstar==0){
                randomAccessFile.seek(pos);
                randomAccessFile.write(user.getBytes("utf-8"));
                randomAccessFile.write(intToByteArray(len0));
                byte[] bytesr=byteMerger(intToByteArray(pos),new byte[]{0,-1},new byte[16],intToByteArray((int) filelen));
                this.ub=new UB(bytesr, (int) filelen);
                randomAccessFile.seek(filelen);
                randomAccessFile.write(bytesr);
                emptyList.add(this.ub);
                this.hashMap=new HashMap<>();
                return this;
            }
//            pos=userstar;
            pos=pos+4+16;
            randomAccessFile.seek(pos);
        }
        randomAccessFile.seek(userstar);
        int len2=0;
        byte[] ubytes=new byte[userLen+4+2+4];
        UB ub=null;
        HashMap hashMap=new HashMap();
        while (len2!=-1){
            len2=randomAccessFile.read(ubytes);
            ub=new UB(ubytes,userstar);
            if (ub.getPermiss()<0){
                emptyList.add(ub);
            }
            hashMap.put(ub.getUser(),ub);
            userstar=ub.next;
            if (userstar>=filelen){
                break;
            }
        }
        this.ub=ub;
        this.hashMap=hashMap;
//        ArrayList arrayList=new ArrayList();
        return this;
    }

    public synchronized void write(String user,byte p) throws IOException {
        if (emptyList.size()>0){
            UB ub=emptyList.getFirst();
            ub.permiss=p;
            randomAccessFile.seek(ub.pos+5);
            randomAccessFile.write(new byte[]{p});
            randomAccessFile.seek(ub.pos+6);
            randomAccessFile.write(user.getBytes("utf-8"));
            hashMap.put(user,ub);
            emptyList.removeFirst();
        }else {
            UB u=hashMap.get(user);
            if (u==null){
                byte[] b1=intToByteArray(ub.next);
                byte[] b2=new byte[]{0,p};
                byte[] b3=user.getBytes("utf-8");
                byte[] b4=intToByteArray(((int)randomAccessFile.length())+4+2+userLen+4);
                byte[] b=byteMerger(b1,b2,b3,b4);
                UB ub=new UB(b,(int)randomAccessFile.length());
                randomAccessFile.seek(randomAccessFile.length());
                randomAccessFile.write(b);
                hashMap.put(user,ub);
            }else {
                change(user,p);
            }
        }
    }
    public synchronized void del(String user) throws IOException {
        UB ub=hashMap.get(user);
        if (ub==null){
            return;
        }
        ub.setPermiss((byte) -1);
        randomAccessFile.seek(ub.getPos()+4+1);
        randomAccessFile.write(new byte[]{-1});
        emptyList.add(ub);
    }
    public synchronized void change(String user,byte p) throws IOException {
        UB ub=hashMap.get(user);
        if (ub==null){
            return;
        }
        ub.setPermiss(p);
        randomAccessFile.seek(ub.getPos()+4+1);
        randomAccessFile.write(new byte[]{p});
        if (p==(byte)-1){
            emptyList.add(ub);
        }
    }
    public byte find(String user){
        UB ub=hashMap.get(user);
        if (ub==null){
            return -1;
        }else {
            return ub.getPermiss();
        }
    }
    //20
    public static RandomAccessFile newFile() throws Exception {
        File file=new File(defPath);
        file.createNewFile();
        RandomAccessFile randomAccessFile=new RandomAccessFile(defPath,"rw");
        int datastar=(userLen+4)*40;
        DataListRW.datastar=datastar;
        byte[] bytes= intToByteArray(datastar);
        randomAccessFile.setLength(datastar);
        randomAccessFile.write(bytes);
        return randomAccessFile;
    }

    public static class UB {
        int last;
        int next;
        int pos;
        byte permiss;
        String user;

        public UB(byte[] bytes, int pos) throws UnsupportedEncodingException {
            this.pos=pos;
            this.last=byteArrayToInt(bytes);
            this.permiss=bytes[5];
            this.user=new String(bytes,4+2,userLen,"UTF-8");
            this.next=byteArrayToInt(bytes,4+2+userLen);
        }
//        public UB(byte[] bytes, int pos) throws UnsupportedEncodingException {
//            this.pos=pos;
//            this.last=byteArrayToInt(bytes);
//            this.permiss=bytes[5];
//            this.user=new String(bytes,4+2,userLen,"UTF-8");
//            this.next=byteArrayToInt(bytes,4+2+userLen);
//        }

        public int getLast() {
            return last;
        }

        public int getNext() {
            return next;
        }

        public byte getPermiss() {
            return permiss;
        }

        public int getPos() {
            return pos;
        }

        public String getUser() {
            return user;
        }

        public void setLast(int last) {
            this.last = last;
        }

        public void setNext(int next) {
            this.next = next;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public void setPermiss(byte permiss) {
            this.permiss = permiss;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
