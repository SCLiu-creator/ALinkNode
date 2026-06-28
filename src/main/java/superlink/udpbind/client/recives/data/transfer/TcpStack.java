package superlink.udpbind.client.recives.data.transfer;

public class TcpStack {

    public TcpStack(){
        len=10240;
        a=new byte[len];
        b=new byte[len];
        buff=a;
    }
    int len;
    int pos=0;
    int mark=0;

    static byte[] a;
    static byte[] b;
    static byte[] buff;
    static byte[] buffer;
    public void in(byte[] b){
        int datalen=b.length;
        if ((pos+datalen)>len){
            int l=len-pos;
            System.arraycopy(b ,0,buff,pos,l);
            pos=pos+l;
            buffer=buff;
            if (a==buff){
                buff=b;
            }else {
                buff=a;
            }
            System.arraycopy(b ,l,buff,0,datalen-l);
            pos=datalen-l;

        }else {
            System.arraycopy(b ,0,buff,pos,b.length);
            pos=pos+datalen;
        }

        String st="a";
        st.split("");
    }

    public void out(byte[] b){
        int datalen=b.length;
        System.arraycopy(buffer ,mark,b,0,datalen);

    }

    public static void main(String[] args) {
        byte b1;
        int i1=129;
        b1= (byte) i1;
        System.out.println(b1);
        i1=b1++;
        System.out.println(i1);

        byte[] by=new byte[]{1,2,3,4,5};
        byte[] y=new byte[5];
        System.arraycopy(by ,0,y,3,by.length);
        System.out.println(buff);


    }
}

