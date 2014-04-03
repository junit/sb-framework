package org.chinasb.common.executor.configuration;

/**
 * 指令拦截器配置
 * @author zhujuan
 */
public class CommandInterceptorConfig {
    private String className;
    private Boolean springBean;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean isSpringBean() {
        return springBean;
    }

    public void setSpringBean(String springBean) {
        this.springBean = Boolean.valueOf(springBean);
    }
}
