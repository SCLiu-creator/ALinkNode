package superlink.filemanage.classprocess.property;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//待注入的bean实体
@Retention(RetentionPolicy.RUNTIME)
public @interface ReInfuse {
    String name() default "";
    String grade() default "a";
}
