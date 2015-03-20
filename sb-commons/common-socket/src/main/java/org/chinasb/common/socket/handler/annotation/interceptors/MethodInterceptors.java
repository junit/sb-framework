package org.chinasb.common.socket.handler.annotation.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.chinasb.common.socket.handler.annotation.CommandInterceptor;

/**
 * 模块方法（指令）拦截器
 * @author zhujuan
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInterceptors {
    /**
     * 拦截器
     * @return
     */
    CommandInterceptor[] value();
}
