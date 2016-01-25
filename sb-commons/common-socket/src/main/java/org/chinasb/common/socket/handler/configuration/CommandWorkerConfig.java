package org.chinasb.common.socket.handler.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 指令工作器配置
 */
public class CommandWorkerConfig {
    /**
     * 热加载模块标识
     */
    private int reloadModuleId;

    /**
     * 全局拦截器集合
     */
    private List<CommandInterceptorConfig> globalInterceptorClasses =
            new ArrayList<CommandInterceptorConfig>();

    /**
     * 获取热加载模块标识
     * @return
     */
    public int getReloadModuleId() {
        return reloadModuleId;
    }

    /**
     * 设置热加载模块标识
     * @param reloadModuleId
     */
    public void setReloadModuleId(int reloadModuleId) {
        this.reloadModuleId = reloadModuleId;
    }

    /**
     * 添加全局拦截器
     * 
     * @param interceptor
     */
    public void addGlobalInterceptor(CommandInterceptorConfig interceptor) {
        globalInterceptorClasses.add(interceptor);
    }

    /**
     * 获取全局拦截器集合
     * 
     * @return
     */
    public List<CommandInterceptorConfig> getGlobalInterceptorClasses() {
        return globalInterceptorClasses;
    }
}
