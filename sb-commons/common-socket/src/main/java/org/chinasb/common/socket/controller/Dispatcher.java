package org.chinasb.common.socket.controller;

import io.netty.channel.Channel;

import org.chinasb.common.socket.handler.CommandHandler;
import org.chinasb.common.socket.message.Request;

/**
 * 消息处理接口
 * 
 * @author zhujuan
 *
 */
public interface Dispatcher {

    /**
     * 添加消息处理器
     * 
     * @param moduleKey 模块标识
     * @param paramHandler 消息处理器
     */
    void put(int moduleKey, CommandHandler handler);

    /**
     * 删除消息处理器
     * 
     * @param moduleKey
     */
    void remove(int moduleKey);

    /**
     * 分发处理
     * 
     * @param session
     * @param paramRequest
     */
    void dispatch(Channel session, Request request);

}
