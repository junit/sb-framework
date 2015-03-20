package org.chinasb.common.socket.handler;

import java.util.Map;

/**
 * 功能模块容器
 * @author zhujuan
 */
public interface CommandWorkerContainer {
    /**
     * 模块指令解析
     * @param clazz
     * @return
     */
	public Map<Integer, CommandResolver> analyzeClass(Class<?> clazz);
    /**
     * 获得指令工作器
     * @param workerClazz
     * @param isSpringBean
     * @return
     */
    public <T> T getWorker(Class<T> workerClazz);
}