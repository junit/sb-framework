package org.chinasb.common.socket.handler.configuration;

/**
 * 指令拦截器配置
 * 
 * @author zhujuan
 */
public class CommandInterceptorConfig {
    /**
     * 拦截器的类名
     */
    private String className;

    /**
     * 获取拦截器的类名
     * 
     * @return
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置拦截器的类名
     * 
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
