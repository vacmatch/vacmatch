package main.scala.util;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Layout {
    static public String DEFAULT_LAYOUT = "layouts/default";

    String value() default DEFAULT_LAYOUT;
}
