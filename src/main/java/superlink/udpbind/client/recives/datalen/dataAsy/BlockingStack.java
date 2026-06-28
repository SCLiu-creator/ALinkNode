package superlink.udpbind.client.recives.datalen.dataAsy;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockingStack {
    private final ArrayBlockingQueue<byte[]> stack = new ArrayBlockingQueue(128);
    private final byte[][] stackb = new byte[128][];
    private final int[] stacki = new int[128];
    {Arrays.fill(stacki,-1);}


    public boolean write(byte[] item) throws InterruptedException {
        stack.put(item);
        return false;
    }
    public int write0(byte[] item) {
        try {
//            stack.put(item);
            return write0(item,0,item.length);
        } catch (Exception interruptedException) {
            interruptedException.printStackTrace();
        }
        return -1;
    }
    public int write0(byte[] item,int start,int len) {
        try {
            stack.put(item);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        return 0;
    }

    int readpos;
    byte[] bytesbuf;
    public int read(byte[] buf) throws InterruptedException {
        int len=0;
        if(bytesbuf!=null){
            while (bytesbuf.length>=readpos){
                if(buf.length>(bytesbuf.length-readpos)){
                    System.arraycopy(bytesbuf,readpos,buf,len,bytesbuf.length-readpos);
                    len=len+bytesbuf.length-readpos;
                    readpos=0;
                    bytesbuf=null;
                    break;
                }else {
                    System.arraycopy(bytesbuf,readpos,buf,len,buf.length-len);
                    readpos=readpos+buf.length-len;
//                    len=;
                    return buf.length;
                }

            }
        }else {
            if(stack.size()==0)return -1;
        }
        boolean ready=false;
        while (stack.size()>0) {
            bytesbuf=stack.poll();
            if(bytesbuf.length==0) {

                if(ready)return len;
                else return -1;
            }
            readpos=0;
            while (bytesbuf != null){
                if(buf.length - len >(bytesbuf.length-readpos)){
                    System.arraycopy(bytesbuf,readpos,buf,len,bytesbuf.length-readpos);
                    len=len+bytesbuf.length-readpos;
                    readpos=0;
                    bytesbuf=null;
                    ready=true;
                }else {
                    System.arraycopy(bytesbuf,readpos,buf,len,buf.length-len);
                    readpos=readpos+buf.length-len;
                    len=buf.length;
                    return len;
                }
            }
        }
        return len; // 或 take() 阻塞等待
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}