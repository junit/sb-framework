package org.chinasb.common.socket.handler;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.digester3.Digester;
import org.chinasb.common.socket.handler.configuration.CommandInterceptorConfig;
import org.chinasb.common.socket.handler.configuration.CommandWorkerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 功能模块元数据解析
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
		return commandWorkerConfig.getDirectory();
	}
	
    @Override
    public List<CommandInterceptorConfig> globalInterceptors() {
        return commandWorkerConfig.getGlobalInterceptorClasses();
    }
}
