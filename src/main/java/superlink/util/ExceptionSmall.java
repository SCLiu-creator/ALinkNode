package superlink.util;

import java.io.IOException;

public class ExceptionSmall extends IOException {
    public ExceptionSmall(){
        super();
    }
    public ExceptionSmall(String message){
        super(message);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        // 重写，禁止抓取堆栈信息
        return this;
    }
}
