package superlink.udpbind.client.server;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import javax.rmi.CORBA.Util;

public class DataPacket {
    public byte[][] bytess;
    public static int fp=1024;

    public boolean over=false;
    public Senders senders;

    int pos0=0;

    public DataPacket(byte[] bytes, UserContext userContext,short id){
        int len=bytes.length/fp;
        int ba=bytes.length%fp;
        if (ba!=0){
            len=len+1;
        }
        bytess=new byte[len][];
        for (int i=0;i<len;i++){
            bytess[i]= Utils.subByte(bytes,i*fp,(i+1)*fp);
        }
        senders=new Senders().InitInit(id,userContext);
    }

    public void send(){
        for (int i = pos0; i <bytess.length&&i< pos0+256; i++) {
            senders.send(Utils.intToByteArray(i),bytess[i]);
        }
    }
    public int getLen(){
        if (bytess.length==0){return 0;}
        return (bytess.length-1)*fp+bytess[bytess.length-1].length;
    }
}
