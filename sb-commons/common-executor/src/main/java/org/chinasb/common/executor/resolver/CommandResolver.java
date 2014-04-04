package org.chinasb.common.executor.resolver;


/**
 * 指令解析器接口
 * @author zhujuan
 */
public interface CommandResolver {
    public void execute(Object... message) throws Exception;
}
