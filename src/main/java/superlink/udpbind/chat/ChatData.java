package superlink.udpbind.chat;

import java.io.File;
import java.util.Objects;

public class ChatData {
    public String u;
    //类型,所属用户状态字
    //0,处理，1，转发，-1，转发自己发送的信息
    public int s=1;
    //记录所属组
    public int n=0;
    public String sn;
    public String date;
    public String text;
    public int tl;
    public String file;
    public int fl;
    //操作状态字,1为删除
    public int i;

    public ChatData(){}

    public ChatData(String user,String date,String text,String file){
        this.u=user;
        this.date=date;
        this.text=text;
        this.file=file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatData data = (ChatData) o;
        if (sn!=null && data.sn!=null){
            if(sn.equals(data.sn)){
                return true;
            }else {
                return false;
            }
        }
        if (data.file==null){
            return Objects.equals(date, data.date) &&
                    Objects.equals(n, data.n) &&
                    Objects.equals(text, data.text)&&
                    Objects.equals(u, data.u);
        }else {
            return Objects.equals(date, data.date) &&
                    Objects.equals(n, data.n) &&
                    Objects.equals(text, data.text) &&
                    Objects.equals(file, data.file) &&
                    Objects.equals(u, data.u);
        }
    }

    public void setSn(){
        if(sn==null||sn.equals("")){
            String fileName= null;
            if (file != null) {
                fileName = new File(file).getName();
            }
            sn=String.valueOf(Objects.hash(text, fileName,u,n)&(((long)date.hashCode())<<24));
        }
    }

    @Override
    public int hashCode() {
        if (sn==null){
            setSn();
        }
        return sn.hashCode();
    }
    public ChatData copy(){
        ChatData data=new ChatData();
        data.date=date;
        data.text=text;
        data.file=file;
        data.fl=fl;
        data.tl=tl;
        data.sn=sn;
        data.u=u;
        data.n=n;
        data.s=s;
        return data;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{")
                .append("\"u\":\"").append(escapeJsonString(u)).append("\",")
                .append("\"s\":").append(s).append(",")
                .append("\"n\":").append(n).append(",")
                .append("\"sn\":\"").append(escapeJsonString(sn)).append("\",")
                .append("\"date\":\"").append(escapeJsonString(date)).append("\",")
                .append("\"text\":\"").append(escapeJsonString(text)).append("\",")
                .append("\"tl\":").append(tl).append(",")
                .append("\"file\":\"").append(escapeJsonString(file)).append("\",")
                .append("\"fl\":").append(fl).append(",")
                .append("\"i\":").append(i)
                .append("}")
                .toString();
    }
    // 简单的JSON字符串转义方法
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
