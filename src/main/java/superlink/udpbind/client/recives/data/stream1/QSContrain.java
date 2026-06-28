package superlink.udpbind.client.recives.data.stream1;

import superlink.util.thread.SThreadPool;
import superlink.util.thread.ThreadFunction;

import java.util.HashMap;
import java.util.Map;

//不使用
public class QSContrain {
    public static Map<String,QSContrain> map=new HashMap<>();

    public static QSContrain getInstance(String username){
        QSContrain qsContrain= map.get(username);
        if (qsContrain==null){
            qsContrain=new QSContrain();
            map.put(username,qsContrain);
        }
        return qsContrain;
    }
    public static QSContrain createQs(String username) throws Exception {
        QSContrain qsContrain=QSContrain.getInstance(username);
        if (qsContrain.rewiter !=null){
            return qsContrain;
        }
        if (qsContrain.reader ==null){
            qsContrain.reader =new QueueStream(username,QueueStream.defsize);
        }

        ThreadFunction function= SThreadPool.create(()->{
            qsContrain.build();
            return null;
        });
        SThreadPool.start(function);

        return qsContrain;
    }

    public QueueStream reader;
    public QueueStream rewiter;

    public void build(){
        synchronized (this){
            while (true){
                reader.build1();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (rewiter !=null){
                    System.out.println("CreateSucceed   ");
                    return;
                }
            }
        }
    }

    public synchronized byte[] synread(){
        return reader.synread();
    }

    public synchronized void synWrite(byte[] bytes){
        rewiter.synWrite(bytes);
    }

    public synchronized void reset(){
        rewiter.reset();
    }

    public synchronized void over(){
        rewiter.over();
    }

    public synchronized void destory(){
        rewiter.re=false;
        rewiter.rcthread.interrupt();
        rewiter=null;
        reader.thread.interrupt();
        reader=null;
    }
}
