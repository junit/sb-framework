package org.chinasb.common.socket.handler;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.chinasb.common.socket.controller.Dispatcher;
import org.chinasb.common.socket.message.Request;
import org.chinasb.common.socket.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 模块方法(指令)抽象
 * @author zhujuan
 */
public abstract class AbstractCommandHandler implements CommandHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 模块方法(指令)解析器集合
     */
    protected Map<Integer, CommandResolver> COMMAND_RESOLVER =
            new ConcurrentHashMap<Integer, CommandResolver>();
    @Autowired
    protected Dispatcher dispatcher;
    @Autowired
    protected CommandWorkerContainer commandWorkerContainer;

    /**
     * 获取功能模块标识
     */
    protected abstract int getModule();

    @PostConstruct
    public void initialize() {
        Map<Integer, CommandResolver> resolvers = commandWorkerContainer.analyzeClass(getClass());
        if (resolvers.size() > 0) {
            for (Entry<Integer, CommandResolver> entry : resolvers.entrySet()) {
                if (entry.getValue() != null) {
                    if (COMMAND_RESOLVER.containsKey(entry.getKey())) {
                        logger.error(String.format("Error: duplicated key [%d]",
                                new Object[] {entry.getKey()}));
                    }
                    COMMAND_RESOLVER.put(entry.getKey(), entry.getValue());
                }
            }
        }
        dispatcher.put(getModule(), this);
    }

    @Override
    public void dispatch(Channel session, Request request) {
        if (request != null) {
            int sn = request.getSn();
            int module = request.getModule();
            int cmd = request.getCmd();
            Response response = Response.valueOf(sn, module, cmd);
            response.setMessageType(request.getMessageType());
            CommandResolver commandResolver = COMMAND_RESOLVER.get(Integer.valueOf(cmd));
            if (commandResolver != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("module:[%d], cmd:[%d]",
                            new Object[] {Integer.valueOf(module), Integer.valueOf(cmd)}));
                }
                try {
                    commandResolver.execute(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.error(String.format("No Invoker for module:[%d], cmd:[%d]",
                            new Object[] {Integer.valueOf(module), Integer.valueOf(cmd)}));
                }
                response.setStatus(-1);
                if (session.isActive()) {
                    session.writeAndFlush(response);
                }
            }
        }
    }
}
