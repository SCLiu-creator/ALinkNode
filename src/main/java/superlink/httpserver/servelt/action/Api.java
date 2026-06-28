package superlink.httpserver.servelt.action;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.)
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String def() default  "";
    String name() default "";

}
