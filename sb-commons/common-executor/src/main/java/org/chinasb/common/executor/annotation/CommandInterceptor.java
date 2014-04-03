package org.chinasb.common.executor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.chinasb.common.executor.Interceptor.Interceptor;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInterceptor {
	Class<? extends Interceptor> value();
	boolean isSpringBean() default false;
}