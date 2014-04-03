package org.chinasb.common.executor.manager;

import org.chinasb.common.executor.resolver.CommandResolver;

/**
 * 指令工作器管理接口
 * @author zhujuan
 */
public interface CommandWorkerManager {
    /**
     * 获得指令解析器
     * @param command
     * @return
     */
	public CommandResolver getResolver(String command);
}
