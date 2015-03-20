package org.chinasb.common.socket.handler;

/**
 * 模块指令解析接口
 * @author zhujuan
 */
public interface CommandResolver {
    /**
     * 执行指令
     * @param message 消息对象
     * @throws Exception
     */
    public void execute(Object message) throws Exception;
}
