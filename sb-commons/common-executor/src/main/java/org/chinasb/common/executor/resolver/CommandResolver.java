package org.chinasb.common.executor.resolver;

import org.chinasb.common.executor.context.Session;

/**
 * 指令解析器接口
 * @author zhujuan
 */
public interface CommandResolver {
    public <T extends Session> void execute(T session);
}
