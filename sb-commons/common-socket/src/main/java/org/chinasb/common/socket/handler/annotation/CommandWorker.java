package org.chinasb.common.socket.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能模块注解
 * @author zhujuan
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandWorker {
    /**
     * 模块标识
     * @return
     */
    int module();
    /**
     * 模块描述
     * @return
     */
	String description() default "";
}