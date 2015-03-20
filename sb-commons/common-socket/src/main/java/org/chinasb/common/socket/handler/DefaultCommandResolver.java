package org.chinasb.common.socket.handler;

import java.lang.reflect.Method;
import java.util.List;

import org.chinasb.common.socket.handler.Interceptor.Interceptor;

/**
 * 模块指令解析
 * @author zhujuan
 */
public class DefaultCommandResolver implements CommandResolver {
    /**
     * 模块标识
     */
    private int module;
    /**
     * 指令标识
     */
    private int cmd;
    /**
     * 指令描述
     */
    private String description;
    /**
     * 实例方法
     */
    private Method m;
    /**
     * 实例对象
     */
    private Object target;
    /**
     * 全局拦截器集合
     */
    private List<Interceptor> globalInterceptors;
    /**
     * 功能模块拦截器集合
     */
    private List<Interceptor> classInterceptors;
    /**
     * 模块方法 (指令 )拦截器集合
     */
    private List<Interceptor> methodInterceptors;

    public DefaultCommandResolver(int module, int cmd, String description, Method m,
            Object target, List<Interceptor> globalInterceptors,
            List<Interceptor> classInterceptors, List<Interceptor> methodInterceptors) {
        this.module = module;
        this.cmd = cmd;
        this.description = description;
        this.m = m;
        this.target = target;
        this.globalInterceptors = globalInterceptors;
        this.classInterceptors = classInterceptors;
        this.methodInterceptors = methodInterceptors;
    }

    @Override
    public void execute(Object message) throws Exception {
        if (globalInterceptors != null) {
            for (Interceptor interceptor : globalInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }
        if (classInterceptors != null) {
            for (Interceptor interceptor : classInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }
        if (methodInterceptors != null) {
            for (Interceptor interceptor : methodInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }

        m.invoke(target, message);

        if (methodInterceptors != null) {
            for (Interceptor interceptor : methodInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
        if (classInterceptors != null) {
            for (Interceptor interceptor : classInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
        if (globalInterceptors != null) {
            for (Interceptor interceptor : globalInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
    }

    @Override
    public String toString() {
        return "DefaultCommandResolver [module=" + module + ", cmd=" + cmd
                + ", description=" + description + ", globalInterceptors=" + globalInterceptors
                + ", classInterceptors=" + classInterceptors + ", methodInterceptors="
                + methodInterceptors + "]";
    }
}
