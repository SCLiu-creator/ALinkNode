package superlink.httpserver.servelt.Interceptor.httpInterceptor;

import io.netty.handler.codec.http.FullHttpRequest;
import superlink.httpserver.servelt.Interceptor.WebInterceptor;
import superlink.httpserver.servelt.Interceptor.httpIntercept;

@WebInterceptor(name = "Referer")
public class HttpAdaperInterceptor implements httpIntercept {
    @Override
    public boolean prehandle(FullHttpRequest msg) {
        String s= msg.headers().get("Referer");
        if (s==null){return false;}
        String[] strings=s.split(":");
        if (strings[2].equals("3000")){
            return true;
        }
        return false;
    }
}
