package org.chinasb.common.executor;

/**
 * 指令解析
 * @author zhujuan
 */
public interface CommandResolver {
    /**
     * 指令执行
     * @param message
     * @throws Exception
     */
    public void execute(Object message) throws Exception;
}
