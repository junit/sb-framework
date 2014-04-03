package org.chinasb.common.executor.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 指令工作器配置
 */
public class CommandWorkerConfig {
    private List<String> scanPackages = new ArrayList<String>();
    private List<CommandInterceptorConfig> globalInterceptorClasses = new ArrayList<CommandInterceptorConfig>();

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
