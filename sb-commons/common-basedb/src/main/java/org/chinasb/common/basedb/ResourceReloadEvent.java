package org.chinasb.common.basedb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 资源重载事件
 * @author zhujuan
 */
public class ResourceReloadEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 6279052881822806933L;

    public ResourceReloadEvent(ApplicationContext source) {
        super(source);
    }
}
