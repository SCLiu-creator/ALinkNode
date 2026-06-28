package superlink.httpserver.servelt.Interceptor.httpInterceptor;

import io.netty.handler.codec.http.FullHttpRequest;
import superlink.httpserver.servelt.Interceptor.WebInterceptor;
import superlink.httpserver.servelt.Interceptor.httpIntercept;

@WebInterceptor(name = "headpic")
public class HttpHeadIntercptor implements httpIntercept {
    @Override
    public boolean prehandle(FullHttpRequest msg) {
       boolean b=msg.uri().split("&",1)[0].contains("headPicture");
       b = false;
       if (b){
           return true;
       }else {
           return false;
       }
    }
}
