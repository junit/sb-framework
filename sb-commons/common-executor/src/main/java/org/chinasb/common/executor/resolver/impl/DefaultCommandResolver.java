package org.chinasb.common.executor.resolver.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.chinasb.common.executor.Interceptor.Interceptor;
import org.chinasb.common.executor.context.Session;
import org.chinasb.common.executor.resolver.CommandResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指令解析器
 * @author zhujuan
 */
public class DefaultCommandResolver implements CommandResolver {
    private final static Logger LOG = LoggerFactory.getLogger(DefaultCommandResolver.class);

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
    public <T extends Session> void execute(T session) {
        try {
            // global interceptor
            if (null != globalInterceptors) {
                for (Interceptor interceptor : globalInterceptors) {
                    if (!interceptor.before(session)) return;
                }
            }
            // class interceptor
            if (null != classInterceptors) {
                for (Interceptor interceptor : classInterceptors) {
                    if (!interceptor.before(session)) return;
                }
            }
            // method interceptor
            if (null != methodInterceptors) {
                for (Interceptor interceptor : methodInterceptors) {
                    if (!interceptor.before(session)) return;
                }
            }
            // invoke
            Object[] params = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].isInstance(session)) {
                    params[i] = session;
                }
            }
            m.invoke(target, params);
            // method interceptor
            if (null != methodInterceptors) {
                for (Interceptor interceptor : methodInterceptors) {
                    if (!interceptor.after(session)) return;
                }
            }
            // class interceptor
            if (null != classInterceptors) {
                for (Interceptor interceptor : classInterceptors) {
                    if (!interceptor.after(session)) return;
                }
            }
            // global interceptor
            if (null != globalInterceptors) {
                for (Interceptor interceptor : globalInterceptors) {
                    if (!interceptor.after(session)) return;
                }
            }
        } catch (Exception e) {
            LOG.error("COMMAND[" + session + "] RESOLVER ERROR", e);
        }
    }
}
