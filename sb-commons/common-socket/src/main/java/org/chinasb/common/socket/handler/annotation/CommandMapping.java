package org.chinasb.common.socket.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模块指令注解
 * @author zhujuan
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    /**
     * 指令标识
     * @return
     */
    int cmd();
    /**
     * 指令描述
     * @return
     */
    String description() default "";
}
