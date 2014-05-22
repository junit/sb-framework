package org.chinasb.common.executor;

/**
 * 指令工作器容器
 * @author zhujuan
 */
public interface CommandWorkerContainer {

    /**
     * 获得指令工作器
     * @param workerName
     * @param isSpringBean
     * @return
     */
    public Object getWorker(String workerName, boolean isSpringBean);

    /**
     * 获得指令工作器
     * @param workerClazz
     * @param isSpringBean
     * @return
     */
    public <T> T getWorker(Class<T> workerClazz, boolean isSpringBean);
}