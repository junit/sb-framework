package org.chinasb.common.executor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 指令工作器容器
 * @author zhujuan
 */
public class DefaultCommandWorkerContainer implements CommandWorkerContainer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getWorker(String workerName) {
        if (null == workerName) {
            return null;
        }
        return applicationContext.getBean(workerName);
    }

    @Override
    public Object getWorker(Class<?> workerClass) {
        if (null == workerClass) {
            return null;
        }
        return applicationContext.getBean(workerClass);
    }

//    @Override
//    public Object getWorker(String workerName) {
//        throw new UnsupportedOperationException(
//                "DefaultWorkerContainer.getWorker(String workerName)");
//    }
//
//    @Override
//    public Object getWorker(Class<?> workerClass) {
//        Object o;
//        try {
//            o = workerClass.newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return o;
//    }
}
