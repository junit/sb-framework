package org.chinasb.common.executor;

/**
 * 指令工作器容器
 * @author zhujuan
 */
public interface CommandWorkerContainer {

    /**
     * 获得指令工作器
     * @param workerName
     * @return
     */
    public Object getWorker(String workerName);

    /**
     * 获得指令工作器
     * @param workerClass
     * @return
     */
    public Object getWorker(Class<?> workerClass);
}