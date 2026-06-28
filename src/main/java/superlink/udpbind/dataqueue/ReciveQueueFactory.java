package superlink.udpbind.dataqueue;

import superlink.udpbind.client.recives.MainDataQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ReciveQueueFactory {

    //stringkey为udpdata的request.username,IntegerKey为该端口的任务序号，同任务序号的信息加入队列，任务序号从packet中获取
    public static Map<String,DataQueue> ReciveData =new ConcurrentHashMap<String,DataQueue>();

    //counterpart name and socket
    public static DataQueue getDataQueue(String requestname){
        if(ReciveData.get(requestname)==null){
            DataQueue queue=new DataQueue(requestname);
            queue.quemap.put((byte)0,new LinkedBlockingQueue<>(10));//0是LL保活队列
            ReciveData.put(requestname,queue);
            return queue;
        }else {
            DataQueue queue=ReciveData.get(requestname);
            return queue;
        }
    }

    public static BlockingQueue<byte[]> getQueMap(String requestnmae,byte b){
        DataQueue dataQueue=ReciveData.get(requestnmae);
        Map<Byte,BlockingQueue<byte[]>> quemap=dataQueue.quemap;

        if (-128<b&&b<127){
            return quemap.get(b);
        }else {
            BlockingQueue<byte[]> blockingQueue=new LinkedBlockingQueue<>(100);
            quemap.put(b,blockingQueue);
            return blockingQueue;
        }
    }

    public static void deltask(String requestnmae){
        DataQueue dataQueue=ReciveData.get(requestnmae);
        try {
            dataQueue.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        ReciveData.remove(requestnmae,dataQueue);
    }



}
