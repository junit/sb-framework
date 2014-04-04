package org.chinasb.common.executor.Interceptor;


/**
 * 指令拦截
 * @author zhujuan
 */
public interface Interceptor {
    /**
     * @param message
     * @return true:继续流程;false:流程中断
     */
    public boolean before(Object message);

    /**
     * @param message
     * @return true:继续流程;false:流程中断
     */
    public boolean after(Object message);
}
