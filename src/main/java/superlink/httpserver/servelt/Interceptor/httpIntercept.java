package superlink.httpserver.servelt.Interceptor;

import io.netty.handler.codec.http.FullHttpRequest;

public interface httpIntercept {

    public boolean prehandle(FullHttpRequest msg);
}
