package org.chinasb.common.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    String mapping() default "";
    String description() default "";
    boolean isWildcard() default false;
    int weight() default 0;
}
