package org.chinasb.common.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 指令工作器容器
 * @author zhujuan
 */
@Component
public class DefaultCommandWorkerContainer implements CommandWorkerContainer {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object getWorker(String workerName, boolean isSpringBean) {
        if (null == workerName) {
            return null;
        }
        if (!isSpringBean) {
            throw new UnsupportedOperationException(
                    "DefaultWorkerContainer.getWorker(String workerName)");
        }
        return applicationContext.getBean(workerName);
    }

    @Override
    public <T> T getWorker(Class<T> workerClazz, boolean isSpringBean) {
        if (null == workerClazz) {
            return null;
        }
        if (!isSpringBean) {
            T o;
            try {
                o = workerClazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return o;
        }
        return applicationContext.getBean(workerClazz);
    }
}
