package org.chinasb.common.executor;

import java.io.File;
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
        digester.addSetProperties("executor", "absolute-path", "absolutePath");
        digester.addSetProperties("executor", "working-directory", "workingDirectory");
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
	public String getWorkingDirectory() {
		return commandWorkerConfig.isAbsolutePath() ? commandWorkerConfig
				.getWorkingDirectory() : System.getProperty("user.dir")
				+ File.separator + commandWorkerConfig.getWorkingDirectory();
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
