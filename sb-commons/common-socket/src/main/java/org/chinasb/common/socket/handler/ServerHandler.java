package org.chinasb.common.socket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.SessionManager;
import org.chinasb.common.socket.controller.Dispatcher;
import org.chinasb.common.socket.firewall.ClientType;
import org.chinasb.common.socket.firewall.Firewall;
import org.chinasb.common.socket.message.Request;
import org.chinasb.common.socket.message.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ServerHandler
 * 
 * @author zhujuan
 *
 */
@Component
public class ServerHandler extends ChannelHandlerAdapter {

    private static final Log LOGGER = LogFactory.getLog(ServerHandler.class);

    @Autowired
    private Firewall firewall;
    @Autowired
    protected Dispatcher dispatcher;
    @Autowired
    protected SessionManager sessionManager;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        StringBuilder sb = new StringBuilder();
        Throwable ex = cause;
        while (ex != null) {
            StackTraceElement[] stackTrace = ex.getStackTrace();
            for (StackTraceElement st : stackTrace) {
                sb.append("\t").append(st.toString()).append("\n");
            }
            if (ex == ex.getCause()) {
                break;
            }
            ex = ex.getCause();
            if (ex != null) {
                sb.append("CAUSE\n").append(ex.getMessage()).append(ex).append("\n");
            }
        }
        LOGGER.error(String.format("Error: %s\n%s",
                new Object[] {cause.getMessage(), sb.toString()}));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (firewall.getClientType(ctx.channel()) == ClientType.MIS) {
            sessionManager.put2MisList(ctx.channel());
        } else {
            sessionManager.put2AnonymousList(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionManager.removeFromMisList(ctx.channel());
        sessionManager.removeFromOnlineList(ctx.channel());
        sessionManager.removeFromAnonymousList(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel session = ctx.channel();
            if (firewall.getClientType(session) == ClientType.MIS) {
                return;
            }

            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                sessionManager.closeSession(session);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("会话:[%s] 进入空闲状态关闭", new Object[] {session}));
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ((msg != null) && ((msg instanceof Request))) {
            long startTime = System.currentTimeMillis();
            Request request = (Request) msg;
            dispatcher.dispatch(ctx.channel(), request);
            long endTime = System.currentTimeMillis();
            if (LOGGER.isDebugEnabled()) {
                Long playerId = sessionManager.getPlayerId(ctx.channel());
                LOGGER.debug(String.format(
                        "角色:[%d] 发送Module:[%d] Cmd:[%d] 响应请求耗时:[%d] 毫秒",
                        new Object[] {playerId, Integer.valueOf(request.getModule()),
                                Integer.valueOf(request.getCmd()),
                                Long.valueOf(endTime - startTime)}));
            }
        } else {
            LOGGER.error("Wrong message type!");
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        if ((msg != null) && (msg.getClass() == Response.class) && (LOGGER.isDebugEnabled())) {
            Response response = (Response) msg;
            int cmd = response.getCmd();
            int module = response.getModule();
            LOGGER.debug(String.format(
                    "Module: [%d]  Cmd: [%d], 包大小:[%d 字节]",
                    new Object[] {Integer.valueOf(module), Integer.valueOf(cmd),
                            Integer.valueOf(msg.toString().getBytes().length)}));
        }
    }
}
