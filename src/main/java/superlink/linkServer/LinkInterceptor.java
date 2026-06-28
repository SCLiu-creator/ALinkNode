package superlink.linkServer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface LinkInterceptor {
    String name() default "";
}
