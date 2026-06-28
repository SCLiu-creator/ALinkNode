package superlink.udpbind.client.recives.datalen;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.dataAsy.BlockingStack;
import superlink.util.Utils;

public class AsySteamCon {
    public AsySteam asySteam1;
    public AsySteam asySteam2;

    BlockingStack blockingStack;

    public AsySteamCon(UserContext userContext){
        short id1 = userContext.newQueue();
        short id2 = userContext.newQueue();
        userContext.stableSend(Utils.byteMerger("ls".getBytes(),Utils.shortToByteArray(id1),Utils.shortToByteArray(id2)));
        asySteam1 = new AsySteam(userContext.userName, id1);
        blockingStack = asySteam1.getWrite();
        asySteam2 = new AsySteam(userContext.userName, id2);
        asySteam2.reqData(null);
    }

    public BlockingStack getBlockingStack() {
        return blockingStack;
    }
}

