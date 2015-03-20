package org.chinasb.common.socket.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 应用程序上下文信息
 * @author zhujuan
 *
 */
public class ApplicationContext {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * Spring容器上下文信息
     */
    private AbstractApplicationContext springContext;

    /**
     * 获取Spring容器上下文信息
     * @return
     */
    private org.springframework.context.ApplicationContext getSpringContext() {
        if (springContext != null) {
            return springContext;
        }
        throw new RuntimeException("ServerContext did not initialized!!");
    }

    /**
     * 应用初始化
     */
    public void initialize() {
        springContext =
                new ClassPathXmlApplicationContext(new String[] {"applicationContext*.xml"});
        springContext.registerShutdownHook();
    }
    
    /**
     * 应用销毁
     */
    public void destory() {
        springContext.stop();
    }

    /**
     * 获取实例
     * @param name
     * @return
     */
    public Object getBean(String name) {
        return getSpringContext().getBean(name);
    }

    /**
     * 获取实例
     * @param beanClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanClazz) {
        String[] names = getSpringContext().getBeanNamesForType(beanClazz);
        if ((names != null) && (names.length > 0)) {
            if (names.length == 1) {
                return (T) getBean(names[0]);
            }
            LOGGER.error("interface class[{}] too many implements bound !!", beanClazz);
        } else {
            LOGGER.error("bean or interface class[{}] NOT bound !!", beanClazz);
        }
        return null;
    }
}
