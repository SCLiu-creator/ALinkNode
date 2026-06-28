package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.dataReq.*;
import superlink.udpbind.usedata.DataRequest;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

//废弃
public class DataSmall extends DataLength {
    public Senders senders;
    public ByteBufer blockingQueue;
    public byte[] rev;
    public long createTime=System.currentTimeMillis();

    public DataSmall(String username) throws Exception {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue();
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
    }

    public DataSmall(String username, int id) throws Exception {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
    }

    public ByteBuffer reqFile(String remoteFilename) {
        superlink.udpbind.usedata.DataRequest sdr = new superlink.udpbind.usedata.DataRequest();
        sdr.requestname = UDPclient.userlocal.username;
        sdr.filename = remoteFilename;
        sdr.id = id;

        byte[] dt = ("Dr" + JSON.toJSONString(sdr)).getBytes();
        Long time = System.currentTimeMillis();
        senders.sendSym(dt);
        byte[] star = null;
        int i = 0;
        superlink.udpbind.usedata.DataRequest dataRequest;
        String re;
        while (true) {

            try {
                star = blockingQueue.poll(2, TimeUnit.SECONDS);
                re = new String(star);
                Long t2 = System.currentTimeMillis();
                time = t2 - time;
                dataRequest=JSON.parseObject(re.substring(2), DataRequest.class);
                break;
            } catch (Exception e) {
                if (i >= 2) {
                    return null;
                }
            }
            senders.sendSym(dt);
            senders.sendSym(dt);
            i++;
        }

        if ("DB".equals(re.substring(0,2))){
            rev=new dataBytes(this).reqFile(dataRequest,time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
            return buffer;//new BufferedInputStream(new FileInputStream(rev));
        }else if ("SB".equals(re.substring(0,2))){
            rev=new dataShorts(this).reqFile(dataRequest,time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
            return buffer;//new ByteArrayInputStream(rev);
        }
        return null;
    }

    public void sends(String username,superlink.udpbind.usedata.DataRequest sdr){
        this.sdr=sdr;
        mapThreadPool.reExecute(username,this);
    }

    long B=1449*256;

    @Override
    public void run() {
        File file=new File(sdr.filename);
        if (file.exists()){
            if (file.length()<B){
                new dataBytes(this).sends(sdr);
            }else {
                new dataShorts(this).sends(sdr);
            }
        }
    }

    @Override
    public int hashCode(){
        return userContext.getBothId()^id;
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    @Override
    public void finalize(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);new Exception("追踪抛出").printStackTrace();
    }
    @Override
    public int clear(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
        return id;
    }
    public int getprogress(){
        if (data instanceof byte[][]){
            byte[][] bs= (byte[][]) data;
            return (int)Math.floor(bs.length*100/sdr.page*1442);
        }
        if (data instanceof File){
            File file= (File) data;
            return (int)Math.floor(file.length()*100/sdr.page*1445);

        }
        return 0;
    }

}
