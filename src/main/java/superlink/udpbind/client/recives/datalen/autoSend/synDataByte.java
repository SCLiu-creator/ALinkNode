package superlink.udpbind.client.recives.datalen.autoSend;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteQueFactory;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoData;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandler;

import java.util.concurrent.Callable;

public class synDataByte implements Callable {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    public int fp = 1449;
    AutoData dataAuto;
    Object object;
    AsynHandler[] callables;

    public synDataByte(AutoData dataAuto, int max) {
        System.out.println("revor");
        this.userContext = dataAuto.userContext;
        this.id = dataAuto.id;
        blockingQueue = dataAuto.blockingQueue;
        this.senders = dataAuto.senders;
        this.dataAuto = dataAuto;
        fp = max - 1;
        bytess = new byte[dataRequest.page][];
        cheak = Utils.byteMerger(("DA").getBytes(), Utils.shortToByteArray((short) id));

    }
    public void synReqFile(DataRequest dataRequest, long timeLong) {
        this.dataRequest = dataRequest;
        this.timeLong=timeLong;
        if (dataRequest.page > 0) {
            dataAuto.data = bytess;
            dataAuto.sdr.page = dataRequest.page;
            userContext = dataAuto.userContext;
            object=rev;
//            cheak=dataAuto.
        }
        ByteQueFactory.alist.add(this);
    }

    public synDataByte addWork(AsynHandler... callables){
        this.callables=callables;
        return this;
    }


        byte[][] bytess = null;
        long timeLong;

        byte[] cheak;
        int i = 0;
        DataRequest dataRequest;
        int j = 0;
        byte[] rev;
        long l1 = 10;


        @Override
        public Object call() {
            int pos = 0;
            byte[] bytes;
            int l;
            byte[] bytec = new byte[]{'A', 'D'};
            while (true) {
                bytes = blockingQueue.poll();
                if (bytes == null) {
                    j++;
                    if (j % 4 == 0 && j != 0) {
                        if (this.userContext.cheak()) {
                            throw new IllegalStateException();
                        } else {
                            senders.sendSym(cheak);
                            if (userContext.getQueue((short) id) != blockingQueue) {
                                throw new IllegalStateException();
                            } else {
                                this.userContext = UDPclient.mainDataQueue.getUserContext(userContext.getUserId());
                                senders.InitInit(id, userContext);
                            }
                        }
                        if (j > 14) {
                            throw new IllegalStateException();
                        }
                    }
                    break;
                }

                if (bytes.length > 5) {
                    l = (int) Utils.calculateChecksum(bytes, 0, bytes.length - 4);
                    if (l == Utils.byteArrayToInt(bytes, bytes.length - 4)) {
                        bytess[bytes[0] + 128] = Utils.subByte(bytes, 1, bytes.length - 5);
                    }
                } else {
                    if (bytes.length == 2 && Utils.equals(bytes, bytec)) {
                        throw new IllegalStateException();
                    }
                }
                j = 0;
            }

            if (j==0 || j/3==0){
                if((System.currentTimeMillis()-l1)>timeLong){
                    pos = Byte.MIN_VALUE;
                    System.out.println("revcheak");
                    for (byte[] b : bytess) {
                        if (b != null) {
                            i++;
                        } else {
                            senders.send(new byte[]{(byte) pos});
                        }
                        pos++;
                    }
                    if (i == dataRequest.page) {
                        try {
                            l = bytess[dataRequest.page - 1].length;
                            l = ((dataRequest.page - 1) * fp) + l;
                            rev = new byte[l];
                            i = 0;
                            for (byte[] bytes1 : bytess) {
                                System.arraycopy(bytes1, 0, rev, i * fp, bytes1.length);
                                i++;
                            }
                            senders.send("OK".getBytes());
                            senders.send("OK".getBytes());
                            return rev;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    l1=System.currentTimeMillis();
                }
            }
            return null;
        }


}
