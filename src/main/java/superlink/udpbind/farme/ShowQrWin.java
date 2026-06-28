package superlink.udpbind.farme;

import com.google.zxing.WriterException;
import superlink.util.GeneratorQR;

import java.io.IOException;

public class ShowQrWin extends ShowQr{

    public byte[] show(String s){
        byte[] bytes=null;
        try {
            bytes=GeneratorQR.getQRCodeImage(s,126,126);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] show(Object o1,Object o2,String s){
        byte[] bytes=null;
        try {
            bytes=GeneratorQR.getQRCodeImage(s,126,126);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
