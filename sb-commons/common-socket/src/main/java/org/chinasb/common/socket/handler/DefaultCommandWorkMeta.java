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
 * 一个默认的指令工作器元数据解析器
 * 
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
        digester.addSetProperties("executor", "reloadModuleId", "reloadModuleId");
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
    public int getReloadModuleId() {
        return commandWorkerConfig.getReloadModuleId();
    }

    @Override
    public List<CommandInterceptorConfig> globalInterceptors() {
        return commandWorkerConfig.getGlobalInterceptorClasses();
    }
}
