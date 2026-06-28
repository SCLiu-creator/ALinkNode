package superlink.udpbind.usedata;

public class DataRequest {
    public int page=-1;
    public String requestname;//发送方名称（本分名称）
    public String filename;
    public String dir;
    public int id;
    public long l=-1;
    public int pl=-1;
    @Override
    public int hashCode(){
        return (requestname+filename).hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    @Override
    public String toString() {
        return "DataRequest{" +
                "page=" + page +
                ", requestname='" + requestname + '\'' +
                ", filename='" + filename + '\'' +
                ", dir='" + dir + '\'' +
                ", id=" + id +
                ", l=" + l +
                ", pl=" + pl +
                '}';
    }
}
