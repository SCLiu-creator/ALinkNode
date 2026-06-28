package superlink.linkServer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Links {
    String name() default "";
    String grade() default "a";
}