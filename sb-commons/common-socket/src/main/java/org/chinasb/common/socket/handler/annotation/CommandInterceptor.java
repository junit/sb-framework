package org.chinasb.common.socket.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.chinasb.common.socket.handler.Interceptor.Interceptor;

/**
 * 拦截器注解
 * @author zhujuan
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInterceptor {
    /**
     * 拦截器
     * @return
     */
	Class<? extends Interceptor> value();
	/**
	 * 拦截器描述
	 * @return
	 */
	String description() default "";
}