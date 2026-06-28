package superlink.udpbind.chat;

import superlink.udpbind.client.UDPclient;

import java.util.HashMap;
import java.util.Map;

public class ChatContrain {
    public static Map<String,ChatBin> chatBinMap=new HashMap<>();

    public static Map<String, ChatGroup> chatBinsMap=new HashMap<>();

    public static ChatGroupSelf chatGroup=null;

    public static ChatBin getChatBin(String username) {
        ChatBin chatBin=chatBinMap.get(username);
        if (chatBin==null){
            chatBin=new ChatBin(username);
            chatBinMap.put(username,chatBin);
        }
        return chatBin;
    }

    public static ChatGroupSelf getSelfChatGroup() {
        if (chatGroup==null){
            chatGroup=new ChatGroupSelf();
        }
        return chatGroup;
    }

    public static ChatGroup getChatGroups(String username) {
        ChatGroup chatGroup=chatBinsMap.get(username);
        if (chatGroup==null){
            chatGroup=new ChatGroup(username);
            chatBinsMap.put(username,chatGroup);
        }
        return chatBinsMap.get(username);
    }
    public static ChatGs getChatGroups(String username, int id) {
        if(username.equals(UDPclient.userlocal.username)){
            return getSelfChatGroup().getCGS(id);
        }
        ChatGroup chatGroup=chatBinsMap.get(username);
        if (chatGroup==null){
            chatGroup=new ChatGroup(username);
            chatBinsMap.put(username,chatGroup);
        }
        ChatGs chatGs = chatGroup.getCGS(id);
        return chatGs;
    }
}
