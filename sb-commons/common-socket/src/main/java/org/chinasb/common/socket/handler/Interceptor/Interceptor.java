package org.chinasb.common.socket.handler.Interceptor;

/**
 * 拦截器接口
 * @author zhujuan
 */
public interface Interceptor {
    /**
     * 拦截前处理
     * @param message
     * @return true:继续流程; false:流程中断
     */
    public boolean before(Object message);

    /**
     * 拦截后处理
     * @param message
     * @return true:继续流程; false:流程中断
     */
    public boolean after(Object message);
}
