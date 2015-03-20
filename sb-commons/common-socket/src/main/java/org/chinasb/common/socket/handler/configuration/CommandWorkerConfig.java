package org.chinasb.common.socket.handler.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能模块配置
 */
public class CommandWorkerConfig {
    /**
     * 热加载功能标志
     */
	private boolean reloadable = true;
	/**
	 * 热加载脚本目录
	 */
	private String directory;
	/**
	 * 全局拦截器集合
	 */
    private List<CommandInterceptorConfig> globalInterceptorClasses = new ArrayList<CommandInterceptorConfig>();

    /**
     * 检测是否启用热加载功能
     * @return
     */
	public boolean isReloadable() {
		return reloadable;
	}

	/**
	 * 设置热加载功能标志
	 * @param reloadable
	 */
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	/**
	 * 获取热加载脚本目录 
	 * @return
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * 设置热加载脚本目录 
	 * @param directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * 添加拦截器到全局拦截器集合
	 * @param interceptor
	 */
    public void addGlobalInterceptor(CommandInterceptorConfig interceptor) {
        globalInterceptorClasses.add(interceptor);
    }

    /**
     * 获取全局拦截器集合
     * @return
     */
    public List<CommandInterceptorConfig> getGlobalInterceptorClasses() {
        return globalInterceptorClasses;
    }
}
