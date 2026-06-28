package superlink.udpbind.usedata;

import java.net.InetAddress;

public class RSRequest implements baseMassage{
    public String username;//发起方名称
    public String rename;//接收方名称
    public InetAddress requestaddress;//发起者外部ip
    public int requestport;
    public InetAddress toaddress;//接收者ip
    public int toport;
    public InetAddress inaddress;//发起者本地ip
    public int inport;
    public boolean request=false;
    public int userid;
    public int bothid;
    public int choose;


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

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
