package org.chinasb.common.executor.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 指令工作器配置
 */
public class CommandWorkerConfig {
	private boolean absolutePath;
	private String workingDirectory;
    private List<String> scanPackages = new ArrayList<String>();
    private List<CommandInterceptorConfig> globalInterceptorClasses = new ArrayList<CommandInterceptorConfig>();

	public boolean isAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = Boolean.parseBoolean(absolutePath);
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public List<String> getScanPackages() {
        return scanPackages;
    }

    public void addScanPackage(String scanPackage) {
        scanPackages.add(scanPackage);
    }

    public void addGlobalInterceptor(CommandInterceptorConfig interceptor) {
        globalInterceptorClasses.add(interceptor);
    }

    public List<CommandInterceptorConfig> getGlobalInterceptorClasses() {
        return globalInterceptorClasses;
    }
}
