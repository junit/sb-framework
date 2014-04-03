package org.chinasb.common.executor.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The spring application context.
 * @author zhujuan
 */
public class AppContext implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object getBean(String beanName) {
        if (null == beanName) {
            return null;
        }
        return applicationContext.getBean(beanName);
    }

    public <T> T getBean(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }
}
