package superlink.testjava;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.usedata.User;

public class I1 implements Ii{
    public Integer a=0;
    public User o;
    public I1(Integer a){
        System.out.println(a);
        this.a=a;
        System.out.println(a);
        System.out.println(this.a);
    }


    public I1() {

    }
    public I1(User s) {
        System.out.println(s);
        o=s;
        System.out.println(s);
    }

    @Override
    public void ll() {
        System.out.println("bb");
    }

    public void sss(){
        System.out.println("ssss");
        System.out.println(o);
        o.port=1;
        System.out.println(o);
        System.out.println(JSON.toJSONString(o));
    }

    public void ss(){
        a=a+1;
    }
}
