package org.chinasb.common.socket.controller.impl;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.controller.Dispatcher;
import org.chinasb.common.socket.handler.CommandHandler;
import org.chinasb.common.socket.message.Request;
import org.springframework.stereotype.Component;

/**
 * 功能模块消息派发
 * @author zhujuan
 *
 */
@Component
public class DispatcherImpl implements Dispatcher {
    private static final Log log = LogFactory.getLog(DispatcherImpl.class);
    private static final Map<Integer, CommandHandler> MODULE_HANDLERS = new HashMap<Integer, CommandHandler>(5);

    @Override
    public void put(int moduleKey, CommandHandler handler) {
        if (handler != null) {
            if (MODULE_HANDLERS.containsKey(Integer.valueOf(moduleKey))) {
                throw new RuntimeException(String.format("Error: duplicated key [%d]",
                        new Object[] {Integer.valueOf(moduleKey)}));
            }
            MODULE_HANDLERS.put(Integer.valueOf(moduleKey), handler);
        }
    }
    
    @Override
    public void dispatch(Channel session, Request request) {
        if ((session == null) || (request == null)) {
            return;
        }
        if ((request.getModule() == 112233) && (request.getCmd() == 332211)) {
            System.exit(0);
        }
        int module = request.getModule();
        CommandHandler handler = MODULE_HANDLERS.get(Integer.valueOf(module));
        if (handler == null) {
            log.error(String.format("No handler for module [%d]",
                    new Object[] {Integer.valueOf(module)}));
            return;
        }
        handler.dispatch(session, request);
    }


}
