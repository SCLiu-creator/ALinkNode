package superlink.filemanage.classprocess.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//待注入点
@Retention(RetentionPolicy.RUNTIME)
public @interface reInject {
    String name() default "";
}
