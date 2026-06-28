package superlink.udpbind.client.recives;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;

public class DataLenMange {

    public static int len0 = 1450;
    public static int len1 = 1450;//65507
    public static int len2 = 65407;//65407
    public static int len3 = 1024;//570;//576

    public static void setLen(int len, int i) {
        if (len > 65507) {
            len = 65500;
        }
        if (len < 578 && len <= 0) {
            len = 578;
        }
        switch (i) {
            case 0:
                len0 = len;
                break;
            case 1:
                len1 = len;
                break;
            case 2:
                len2 = len;
                break;
            case 3:
                len3 = len;
                break;
        }
    }

    public static int getLen(String user) {
        UserContext userContext = UDPclient.mainDataQueue.getUserContext(user);
        if (userContext == null) return len0;
        switch (userContext.sort) {
            case -1:
            case 1:
                return len1;
            case -2:
            case 2:
                return len2;
            case -3:
            case 3:
                return len3;
            default:
                return len0;
        }
    }
    public static int getLen(int i) {
        switch (i) {
            case -1:
            case 1:
                return len1;
            case -2:
            case 2:
                return len2;
            case -3:
            case 3:
                return len3;
            default:
                return len0;
        }
    }
}
