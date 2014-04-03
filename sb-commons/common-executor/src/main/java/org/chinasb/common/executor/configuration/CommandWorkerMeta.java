package org.chinasb.common.executor.configuration;

import java.util.List;


/**
 * 指令工作器元数据接口
 * @author zhujuan
 */
public interface CommandWorkerMeta {
	public boolean isScanPackage(String packageName);
	public List<CommandInterceptorConfig> globalInterceptors();
}
