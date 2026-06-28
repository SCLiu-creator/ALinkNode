package superlink.filemanage.classprocess.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//用于注入
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String name() default "";
}
