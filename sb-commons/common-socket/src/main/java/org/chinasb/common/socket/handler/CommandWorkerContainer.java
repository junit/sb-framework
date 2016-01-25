package org.chinasb.common.socket.handler;

import java.util.Map;

/**
 * 指令工作器容器
 * 
 * @author zhujuan
 */
public interface CommandWorkerContainer {
    /**
     * 模块分析
     * 
     * @param clazz
     * @return
     */
    public Map<Integer, CommandResolver> analyzeClass(Class<?> clazz);

    /**
     * 获得指令工作器实例
     * 
     * @param workerClazz
     * @return
     */
    public <T> T getWorker(Class<T> workerClazz);
}
