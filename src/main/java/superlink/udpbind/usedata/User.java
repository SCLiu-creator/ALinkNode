package superlink.udpbind.usedata;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import superlink.util.JackJson;

import java.net.InetAddress;

public class User implements baseMassage{
    public String nickName="";
    public String username;
    public InetAddress address;//公网ip
    public int port;
    public InetAddress inaddress;//本地ip
    public int inport;
    public boolean request=false;
    public int choose=0;
    public int udpstate;

//    @JSONField(serialize = false,deserialize = false)
    public void setAddress(InetAddress address) {
        this.inaddress = address;
    }

//    @JSONField(serialize = false,deserialize = false)
    public InetAddress getInaddress() {
        return inaddress;
    }

    @JSONField(serialize = false,deserialize = false)
    public long index;
    @JSONField(serialize = false,deserialize = false)
    public int time=0;
//    public int tcpstate;
//    public int tcpstate2;

    public User copy(){
        User user=new User();
        user.nickName=nickName;
        user.username=username;
        user.address=address;
        user.port=port;
        user.inaddress=inaddress;
        user.inport=inport;
        user.request=request;
        user.choose=choose;
        user.udpstate=udpstate;
        return user;
    }


    @Override
    public boolean equals(Object user){
        if (((User)user).username.equals(this.username)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode(){
     return username.hashCode();
    }

    @Override
    public String toString() {
        // 手动拼接 JSON 字符串
        StringBuilder jsonBuilder = new StringBuilder(200);

        jsonBuilder.append("{");

        jsonBuilder.append("\"nickName\":\"").append(nickName != null ? nickName : "").append("\",");
        jsonBuilder.append("\"username\":\"").append(username != null ? username : "").append("\",");

        if (address != null) {
            jsonBuilder.append("\"address\":\"").append(address.getHostAddress()).append("\",");
        } else {
            jsonBuilder.append("\"address\":\"\",");
        }

        jsonBuilder.append("\"port\":").append(port).append(",");

        if (inaddress != null) {
            jsonBuilder.append("\"inaddress\":\"").append(inaddress.getHostAddress()).append("\",");
        } else {
            jsonBuilder.append("\"inaddress\":\"\",");
        }

        jsonBuilder.append("\"inport\":").append(inport).append(",");
        jsonBuilder.append("\"choose\":").append(choose).append(",");
        jsonBuilder.append("\"udpstate\":").append(udpstate);

        // 移除最后一个逗号（如果有的话），然后关闭JSON对象
//        int length = jsonBuilder.length();
//        if (length > 1 && jsonBuilder.charAt(length - 1) == ',') {
//            jsonBuilder.setLength(length - 1);
//        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }




    public static void main(String[] args) throws JsonProcessingException {
String s="{\"address\":\"112.24.192.131\"" +
        ",\"choose\":0," +
        "\"inaddress\":\"192.168.0.120\"," +
        "\"inport\":6049," +
        "\"nickName\":\"我的电脑\"," +
        "\"port\":16404," +
        "\"request\":false," +
        "\"udpstate\":0," +
        "\"username\":\"81ZO0d7Bjj9StquD\"}";
    int l=s.getBytes().length;
    User user= (User) JackJson.toObject(s,User.class);
    int i=10000000;
    long t1=System.currentTimeMillis();

    while (i>0){
        i--;
//        String s2=JSON.toJSONString(user);//4319
//        String s2=JackJson.toJsonfast(user);//1322
//         String s2=user.toString();//3568

    }
    System.out.println(System.currentTimeMillis()-t1);
    }
}

