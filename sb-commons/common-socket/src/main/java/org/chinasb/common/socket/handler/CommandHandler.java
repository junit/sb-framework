package org.chinasb.common.socket.handler;

import io.netty.channel.Channel;

import org.chinasb.common.socket.message.Request;

/**
 * 指令处理器接口
 * 
 * @author zhujuan
 */
public interface CommandHandler {

    /**
     * 分发处理
     * 
     * @param session
     * @param request
     */
    public void dispatch(Channel session, Request request);
}
