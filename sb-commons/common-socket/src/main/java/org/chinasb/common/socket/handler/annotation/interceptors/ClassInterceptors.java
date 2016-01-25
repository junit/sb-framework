package org.chinasb.common.socket.handler.annotation.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.chinasb.common.socket.handler.annotation.CommandInterceptor;

/**
 * 模块拦截器注解
 * 
 * @author zhujuan
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassInterceptors {
    /**
     * 拦截器
     * 
     * @return
     */
    CommandInterceptor[] value();
}
