package org.chinasb.common.executor.configuration.impl;

import java.util.List;

import org.apache.commons.digester3.Digester;
import org.chinasb.common.executor.configuration.CommandInterceptorConfig;
import org.chinasb.common.executor.configuration.CommandWorkerConfig;
import org.chinasb.common.executor.configuration.CommandWorkerMeta;

/**
 * 指令工作器元数据
 * @author zhujuan
 */
public class DefaultCommandWorkMeta implements CommandWorkerMeta {
    private CommandWorkerConfig commandWorkerConfig;

    public DefaultCommandWorkMeta(String configFilePath) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("executor", CommandWorkerConfig.class);
        digester.addCallMethod("executor/worker-scan", "addScanPackage", 0);
        digester.addObjectCreate("executor/worker-global-interceptor",
                CommandInterceptorConfig.class);
        digester.addSetProperties("executor/worker-global-interceptor");
        digester.addSetNext("executor/worker-global-interceptor", "addGlobalInterceptor");
        try {
            commandWorkerConfig =
                    ((CommandWorkerConfig) digester.parse(DefaultCommandWorkMeta.class
                            .getResourceAsStream("/" + configFilePath)));
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO PARSE[' " + configFilePath + " ']", e);
        }
    }

    @Override
    public boolean isScanPackage(String packageName) {
        for (String s : commandWorkerConfig.getScanPackages()) {
            if (packageName.startsWith(s)) return true;
        }
        return false;
    }

    @Override
    public List<CommandInterceptorConfig> globalInterceptors() {
        return commandWorkerConfig.getGlobalInterceptorClasses();
    }
}
