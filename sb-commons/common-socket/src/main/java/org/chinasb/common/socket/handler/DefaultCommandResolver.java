package org.chinasb.common.socket.handler;

import io.netty.channel.Channel;

import java.lang.reflect.Method;
import java.util.List;

import org.chinasb.common.socket.handler.Interceptor.Interceptor;
import org.chinasb.common.socket.message.Request;
import org.chinasb.common.socket.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一个默认的指令解析器
 * 
 * @author zhujuan
 */
public class DefaultCommandResolver implements CommandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandResolver.class);

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
     * 模块拦截器集合
     */
    private List<Interceptor> classInterceptors;
    /**
     * 指令拦截器集合
     */
    private List<Interceptor> methodInterceptors;

    public DefaultCommandResolver(int module, int cmd, String description, Method m, Object target,
            List<Interceptor> globalInterceptors, List<Interceptor> classInterceptors,
            List<Interceptor> methodInterceptors) {
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
    public void execute(Channel session, Request request, Response response) {
        try {
            if (globalInterceptors != null) {
                for (Interceptor interceptor : globalInterceptors) {
                    if (!interceptor.before(session, request, response))
                        return;
                }
            }
            if (classInterceptors != null) {
                for (Interceptor interceptor : classInterceptors) {
                    if (!interceptor.before(session, request, response))
                        return;
                }
            }
            if (methodInterceptors != null) {
                for (Interceptor interceptor : methodInterceptors) {
                    if (!interceptor.before(session, request, response))
                        return;
                }
            }

            m.invoke(target, new Object[] {session, request, response});

            if (methodInterceptors != null) {
                for (Interceptor interceptor : methodInterceptors) {
                    if (!interceptor.after(session, request, response))
                        return;
                }
            }
            if (classInterceptors != null) {
                for (Interceptor interceptor : classInterceptors) {
                    if (!interceptor.after(session, request, response))
                        return;
                }
            }
            if (globalInterceptors != null) {
                for (Interceptor interceptor : globalInterceptors) {
                    if (!interceptor.after(session, request, response))
                        return;
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format("module:[%d], cmd:[%d], description:[%s], stack:[%s]",
                    new Object[] {module, cmd, description, e}));
        }
    }

    @Override
    public String toString() {
        return "DefaultCommandResolver [module=" + module + ", cmd=" + cmd + ", description="
                + description + ", globalInterceptors=" + globalInterceptors
                + ", classInterceptors=" + classInterceptors + ", methodInterceptors="
                + methodInterceptors + "]";
    }
}
