package org.chinasb.common.socket.firewall;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.SessionManager;
import org.chinasb.common.socket.codec.RequestDecoder;
import org.chinasb.common.socket.type.SessionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 防火墙字节数据过滤处理
 * @author zhujuan
 *
 */
@Component
public class ByteAttackFilter extends ChannelHandlerAdapter {
    private static final Log LOGGER = LogFactory.getLog(ByteAttackFilter.class);
    @Autowired
    private Firewall firewall;
    @Autowired
    private SessionManager sessionManager;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object message) throws Exception {
        Channel session = ctx.channel();
        if (firewall.getClientType(session) != ClientType.MIS) {
            boolean blocked = firewall.isBlocked(session);
            if ((!blocked) && ((message instanceof ByteBuf))) {
                ByteBuf byteBuf = (ByteBuf) message;
                int length = byteBuf.readableBytes();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("%s", new Object[] {message}));
                }
                blocked = firewall.blockedByBytes(session, length);
            }
            if (blocked) {
                String remoteIp = sessionManager.getRemoteIp(session);
                long playerId = sessionManager.getPlayerId(session).longValue();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("%s", new Object[] {message}));
                }
                LOGGER.error(String.format("In blacklist: [ip: %s, playerId: %d]", new Object[] {
                        remoteIp, Long.valueOf(playerId)}));
                sessionManager.closeSession(session);
                return;
            }
        }
        ctx.fireChannelRead(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel session = ctx.channel();
        int currClients = firewall.increaseClients();
        if ((firewall.getClientType(session) != ClientType.MIS) && (firewall.isBlocked(session))) {
            String remoteIp = sessionManager.getRemoteIp(session);
            Long playerId = sessionManager.getPlayerId(session);
            LOGGER.error(String.format("In blacklist: [ip: %s, playerId: %d]", new Object[] {
                    remoteIp, playerId}));
            sessionManager.closeSession(session);
            return;
        }
        if (firewall.isMaxClientLimit(currClients)) {
            sessionManager.closeSession(session);
            LOGGER.error("Connections limit, close session...");
            return;
        }
        session.attr(SessionType.WHICH_CLIENTS).set(Integer.valueOf(currClients));;
        ctx.fireChannelActive();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel session = ctx.channel();
        if (firewall.getClientType(session) != ClientType.MIS) {
            Integer whichClient = session.attr(SessionType.WHICH_CLIENTS).getAndRemove();
            if ((whichClient != null) && (firewall.isMaxClientActives(whichClient.intValue()))
                    && (firewall.isMaxClientActive())) {
                if (session.isActive()) {
                    LOGGER.info(String.format("SESSION[%s] 发送SOCKET安全策略...",
                            new Object[] {session.id()}));
                    ChannelFuture future = session.write(RequestDecoder.policyResponse);
                    future.addListener(ChannelFutureListener.CLOSE);
                }
                return;
            }
        }
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        firewall.decreaseClients();
        ctx.fireChannelInactive();
    }
}
