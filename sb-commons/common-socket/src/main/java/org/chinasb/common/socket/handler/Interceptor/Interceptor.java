package org.chinasb.common.socket.handler.Interceptor;

import io.netty.channel.Channel;

import org.chinasb.common.socket.message.Request;
import org.chinasb.common.socket.message.Response;

/**
 * 拦截器接口
 * 
 * @author zhujuan
 */
public interface Interceptor {
    /**
     * 拦截前处理
     * 
     * @param session
     * @param request
     * @param response
     * @return true:继续流程; false:流程中断
     */
    public boolean before(Channel session, Request request, Response response);

    /**
     * 拦截后处理
     * 
     * @param session
     * @param request
     * @param response
     * @return true:继续流程; false:流程中断
     */
    public boolean after(Channel session, Request request, Response response);
}
