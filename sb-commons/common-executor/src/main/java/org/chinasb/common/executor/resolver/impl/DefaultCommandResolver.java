package org.chinasb.common.executor.resolver.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.chinasb.common.executor.Interceptor.Interceptor;
import org.chinasb.common.executor.resolver.CommandResolver;

/**
 * 指令解析器
 * @author zhujuan
 */
public class DefaultCommandResolver implements CommandResolver {
    private Method m;
    private Class<?> targetClazz;
    private Class<?>[] paramTypes;
    private Object target;

    private List<Interceptor> globalInterceptors;
    private List<Interceptor> classInterceptors;
    private List<Interceptor> methodInterceptors;

    public DefaultCommandResolver(Method m, Class<?>[] paramTypes, Class<?> targetClazz,
            Object target, List<Interceptor> globalInterceptors,
            List<Interceptor> classInterceptors, List<Interceptor> methodInterceptors) {
        this.m = m;
        this.paramTypes = paramTypes;
        this.targetClazz = targetClazz;
        this.target = target;
        this.globalInterceptors = globalInterceptors;
        this.classInterceptors = classInterceptors;
        this.methodInterceptors = methodInterceptors;
    }

    @Override
    public void execute(Object message) throws Exception {
        // global interceptor
        if (null != globalInterceptors) {
            for (Interceptor interceptor : globalInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }
        // class interceptor
        if (null != classInterceptors) {
            for (Interceptor interceptor : classInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }
        // method interceptor
        if (null != methodInterceptors) {
            for (Interceptor interceptor : methodInterceptors) {
                if (!interceptor.before(message)) return;
            }
        }
        // invoke
//        Object[] params = new Object[paramTypes.length];
//        for (int i = 0; i < paramTypes.length; i++) {
//            if (paramTypes[i].isInstance(message)) {
//                params[i] = message;
//            }
//        }
        m.invoke(target, message);
        // method interceptor
        if (null != methodInterceptors) {
            for (Interceptor interceptor : methodInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
        // class interceptor
        if (null != classInterceptors) {
            for (Interceptor interceptor : classInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
        // global interceptor
        if (null != globalInterceptors) {
            for (Interceptor interceptor : globalInterceptors) {
                if (!interceptor.after(message)) return;
            }
        }
    }
}
