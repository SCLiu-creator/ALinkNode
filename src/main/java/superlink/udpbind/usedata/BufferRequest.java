package superlink.udpbind.usedata;

public class BufferRequest {
    public int page=-1;
    public String name;
    public String bufname;
    public String dir;
    public int mode;
    public int id;
    public long l=-1;
    public int pl=1450;
    public long al;
    public int lock;

    @Override
    public int hashCode(){
        return (name+bufname).hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }
}
