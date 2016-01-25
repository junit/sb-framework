package org.chinasb.common.basedb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 静态资源数据索引声明
 * 
 * @author zhujuan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Index {
    /** 索引名，同一资源的索引名必须唯一 */
    String name();

    /** 排序 */
    int order() default 0;

    /** 表达式 */
    String expression() default "";
}
