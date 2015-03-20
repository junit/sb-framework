package org.chinasb.common.socket.controller;

import io.netty.channel.Channel;

import org.chinasb.common.socket.handler.CommandHandler;
import org.chinasb.common.socket.message.Request;

/**
 * 功能模块消息派发接口
 * @author zhujuan
 *
 */
public interface Dispatcher {
    /**
     * 添加功能模块处理器
     * @param moduleKey 功能模块标识
     * @param paramHandler 功能模块处理器
     */
    public void put(int moduleKey, CommandHandler handler);

    /**
     * 派发消息
     * @param session
     * @param paramRequest
     */
    public void dispatch(Channel session, Request request);
}
