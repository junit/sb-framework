package org.chinasb.common.jreloader.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可重载标识
 * @author zhujuan
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reloadable {
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