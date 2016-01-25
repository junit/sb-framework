package org.chinasb.common.socket.handler;

import java.util.List;

import org.chinasb.common.socket.handler.configuration.CommandInterceptorConfig;

/**
 * 指令工作器元数据接口
 * 
 * @author zhujuan
 */
public interface CommandWorkerMeta {
    /**
     * 获取热加载模块标识
     * 
     * @return
     */
    public int getReloadModuleId();

    /**
     * 获取全局拦截器集合
     * 
     * @return
     */
    public List<CommandInterceptorConfig> globalInterceptors();
}
