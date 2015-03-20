package org.chinasb.common.socket.handler;

import io.netty.channel.Channel;

import org.chinasb.common.socket.message.Request;


/**
 * 模块方法(指令)处理接口
 * @author zhujuan
 */
public interface CommandHandler {
    /**
     * 派发请求消息
     * @param session
     * @param request
     */
    public void dispatch(Channel session, Request request);
}
