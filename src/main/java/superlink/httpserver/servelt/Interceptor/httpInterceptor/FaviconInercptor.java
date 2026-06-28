package superlink.httpserver.servelt.Interceptor.httpInterceptor;

import io.netty.handler.codec.http.FullHttpRequest;
import superlink.httpserver.servelt.Interceptor.WebInterceptor;
import superlink.httpserver.servelt.Interceptor.httpIntercept;

@WebInterceptor(name = "fac")
public class FaviconInercptor implements httpIntercept {
    @Override
    public boolean prehandle(FullHttpRequest msg) {
        String s= msg.uri();
        if (s==null){return false;}
        if (s.contains("favicon.ico")){
            return true;
        }
        return false;
    }
}
