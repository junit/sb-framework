package org.chinasb.common.executor.Interceptor;

import org.chinasb.common.executor.context.Session;

/**
 * 指令拦截
 * @author zhujuan
 */
public interface Interceptor {
    /**
     * @param session
     * @return true:继续流程;false:流程中断
     */
    public boolean before(Session session);

    /**
     * @param session
     * @return true:继续流程;false:流程中断
     */
    public boolean after(Session session);
}
