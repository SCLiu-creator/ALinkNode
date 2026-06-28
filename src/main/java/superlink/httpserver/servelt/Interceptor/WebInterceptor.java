package superlink.httpserver.servelt.Interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface WebInterceptor {
    String name() default "";
}
