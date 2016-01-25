package org.chinasb.common.socket.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 应用环境上下文
 * 
 * @author zhujuan
 *
 */
public class ApplicationContext {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConfigurableApplicationContext ctx;

    private ConfigurableApplicationContext getApplicationContext() {
        if (ctx != null) {
            return ctx;
        }
        throw new RuntimeException("ServerContext did not initialized!");
    }

    /**
     * 应用环境初始化
     */
    public void initialize() {
        ctx = new ClassPathXmlApplicationContext(new String[] {"applicationContext*.xml"});
        ctx.registerShutdownHook();
    }

    /**
     * 应用环境销毁
     */
    public void destory() {
        if (ctx != null) {
            ctx.close();
        }
    }

    /**
     * 获取应用环境实例
     * 
     * @param name 实例名称
     * @return
     */
    public Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 获取应用环境实例
     * 
     * @param beanClazz 实例类
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanClazz) {
        String[] names = getApplicationContext().getBeanNamesForType(beanClazz);
        if ((names != null) && (names.length > 0)) {
            if (names.length == 1) {
                return (T) getBean(names[0]);
            }
            logger.error("interface class[{}] too many implements bound !!", beanClazz);
        } else {
            logger.error("bean or interface class[{}] NOT bound !!", beanClazz);
        }
        return null;
    }
}
