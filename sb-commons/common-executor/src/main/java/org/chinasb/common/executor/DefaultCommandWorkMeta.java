package org.chinasb.common.executor;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.digester3.Digester;
import org.chinasb.common.executor.configuration.CommandInterceptorConfig;
import org.chinasb.common.executor.configuration.CommandWorkerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 指令工作器元数据
 * @author zhujuan
 */
@Component
public class DefaultCommandWorkMeta implements CommandWorkerMeta {
    @Autowired(required = false)
    @Qualifier("commandworker.config_location")
    private String executorConfigLocation = "worker/commandworker-config.xml";
    private CommandWorkerConfig commandWorkerConfig;
    
    @PostConstruct
    protected void initialize() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("executor", CommandWorkerConfig.class);
        digester.addSetProperties("executor", "reloadable", "reloadable");
        digester.addSetProperties("executor", "reload-directory", "directory");
        digester.addCallMethod("executor/worker-scan", "addScanPackage", 0);
        digester.addObjectCreate("executor/worker-global-interceptor",
                CommandInterceptorConfig.class);
        digester.addSetProperties("executor/worker-global-interceptor");
        digester.addSetNext("executor/worker-global-interceptor", "addGlobalInterceptor");
        try {
            commandWorkerConfig =
                    ((CommandWorkerConfig) digester.parse(DefaultCommandWorkMeta.class
                            .getResourceAsStream("/" + executorConfigLocation)));
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO PARSE[' " + executorConfigLocation + " ']", e);
        }
    }
    
	@Override
	public boolean isReloadable() {
		return commandWorkerConfig.isReloadable();
	}
	
	@Override
	public String getDirectory() {
		return commandWorkerConfig.getDirectory() == null ? "scripts"
				: commandWorkerConfig.getDirectory();
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
