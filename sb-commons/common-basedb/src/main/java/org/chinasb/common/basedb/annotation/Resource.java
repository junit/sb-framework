package org.chinasb.common.basedb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源数据对象声明
 * 
 * @author zhujuan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Resource {
    /** 资源文件后缀 */
    String suffix() default "json";

    /** 资源文件类型 */
    String type() default "json";
}
