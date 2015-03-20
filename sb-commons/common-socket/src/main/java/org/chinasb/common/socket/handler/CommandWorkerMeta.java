package org.chinasb.common.socket.handler;

import java.util.List;

import org.chinasb.common.socket.handler.configuration.CommandInterceptorConfig;

/**
 * 功能模块元数据接口
 * @author zhujuan
 */
public interface CommandWorkerMeta {
	/**
	 * 检测是否启用热加载功能
	 * @return
	 */
	public boolean isReloadable();
	/**
	 * 获取热加载脚本目录
	 * @return
	 */
	public String getDirectory();
	/**
	 * 获取全局拦截器集合
	 * @return
	 */
	public List<CommandInterceptorConfig> globalInterceptors();
}
