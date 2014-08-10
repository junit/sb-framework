package org.chinasb.common.executor;

import java.util.List;

import org.chinasb.common.executor.configuration.CommandInterceptorConfig;

/**
 * 指令工作器元数据
 * @author zhujuan
 */
public interface CommandWorkerMeta {
	/**
	 * 获取工作目录
	 * @return
	 */
	public String getWorkingDirectory();
    /**
     * 类包判断
     * @param packageName
     * @return
     */
	public boolean isScanPackage(String packageName);
	/**
	 * 全局拦截器
	 * @return
	 */
	public List<CommandInterceptorConfig> globalInterceptors();
}
