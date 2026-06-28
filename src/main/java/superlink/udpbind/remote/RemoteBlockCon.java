package superlink.udpbind.remote;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.remote.block.RemoteBlock;
import superlink.util.Utils;

import java.util.*;

public class RemoteBlockCon {

    public static HashMap<String, RemoteBlock> hashMap = new HashMap();
    public static LinkedList<UserContext.Task> blockList = new LinkedList();
    public static ArrayList<UserContext.Task> listBuf = new ArrayList<>();

    public static void check() {
        if (listBuf.size() > 0) {
            flaskBlock();
        }
        Iterator<UserContext.Task> iterator = blockList.iterator();
        while (iterator.hasNext()) {
            RemoteBlock block = iterator.next().block;
            UserContext userContext = block.userContext;
            short id = block.id;
            if(userContext.map.size()!=0){
                if (block.change) {
                    userContext.stableSend(Utils.byteMerger("TB".getBytes(),
                            Utils.shortToByteArray(id),
                            Utils.intToByteArray(block.mode)
                    ));
                } else {
                    if (block.task == null) {
                        if (block.isFree()) {
                            userContext.taskMap.remove(id);
                        } else {
                            if (block.timeOut()) {
                                userContext.deltask(id);
                            }
                        }
                    }
                    iterator.remove();
                }
            }else {
                block.change=false;
                block.mode=0;
                block.wake();
                iterator.remove();
            }
        }
    }

    public static void checkClear() {
        MainDataQueue.quemap.forEach((k, user) -> {
            Iterator<Map.Entry<Short, UserContext.Task>> iterator = user.taskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                UserContext.Task task = iterator.next().getValue();
                RemoteBlock block = task.block;
                UserContext userContext = block.userContext;
                short id = block.id;
                if (block.task == null) {
                    if (block.isFree()) {
                        userContext.taskMap.remove(id);
                    } else {
                        if (block.timeOut()) {
                            userContext.deltask(id);
                        }
                    }
                }
            }
        });
    }


    public static synchronized void addBlock(UserContext.Task task) {
        listBuf.add(task);
    }

    private static synchronized void flaskBlock() {
        blockList.addAll(listBuf);
        listBuf.clear();
    }
}
