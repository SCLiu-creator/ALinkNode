package superlink.udpbind.usedata;

import java.net.InetAddress;

public class UserRequest implements baseMassage{
    public String username="no init";
    public InetAddress requestaddress;//发送者ip
    public int requestport;
    public InetAddress toaddress;//接收者ip
    public int toport;
    public InetAddress inaddress;//本地ip
    public int inport;
    public boolean request=false;
    public int userid;
    public int choose;
    public String data;

    @Override
    public String toString() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        jsonBuilder.append("\"username\":\"")
                .append(username != null ? username : "")
                .append("\",");

        jsonBuilder.append("\"requestaddress\":\"")
                .append(requestaddress != null ? requestaddress.getHostAddress() : "")
                .append("\",");

        jsonBuilder.append("\"requestport\":")
                .append(requestport)
                .append(",");

        jsonBuilder.append("\"toaddress\":\"")
                .append(toaddress != null ? toaddress.getHostAddress() : "")
                .append("\",");

        jsonBuilder.append("\"toport\":")
                .append(toport)
                .append(",");

        jsonBuilder.append("\"inaddress\":\"")
                .append(inaddress != null ? inaddress.getHostAddress() : "")
                .append("\",");

        jsonBuilder.append("\"userid\":")
                .append(userid)
                .append(",");

        jsonBuilder.append("\"inport\":")
                .append(inport)
                .append(",");

        jsonBuilder.append("\"request\":")
                .append(request)
                .append(",");

        jsonBuilder.append("\"choose\":")
                .append(choose)
                .append(",");

        jsonBuilder.append("\"data\":\"")
                .append(data != null ? data : "")
                .append("\"");

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
