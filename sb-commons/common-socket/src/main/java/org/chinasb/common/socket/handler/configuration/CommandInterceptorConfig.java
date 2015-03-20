package org.chinasb.common.socket.handler.configuration;

/**
 * 拦截器配置
 * @author zhujuan
 */
public class CommandInterceptorConfig {
    /**
     * 拦截器类名称
     */
    private String className;

    /**
     * 获取拦截器类名称
     * @return
     */
    public String getClassName() {
        return className;
    }

    /**
     * 获取拦截器类名称
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
