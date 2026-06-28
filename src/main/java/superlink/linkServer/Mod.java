package superlink.linkServer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {

    String def() default  "";
    String name() default "";

}
