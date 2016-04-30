package org.chinasb.common.socket.handler;

import org.chinasb.common.socket.message.Request;
import org.chinasb.common.socket.message.Response;

import io.netty.channel.Channel;

/**
 * 指令解析器接口
 * 
 * @author zhujuan
 */
public interface CommandResolver {

    /**
     * 解析执行
     * 
     * @param session
     * @param request
     * @param response
     * @throws Exception 
     */
    public void execute(Channel session, Request request, Response response) throws Exception;
}
