package org.chinasb.common.socket.firewall;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 防火墙指令数据过滤处理
 * 
 * @author zhujuan
 *
 */
@Sharable
@Component
public class CmdAttackFilter extends ChannelInboundHandlerAdapter {
	
    private static final Log LOGGER = LogFactory.getLog(CmdAttackFilter.class);
    
    @Autowired
    private Firewall firewall;
    @Autowired
    private SessionManager sessionManager;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        Channel session = ctx.channel();
        if ((firewall.getClientType(session) != ClientType.MIS)
                && (firewall.blockedByPacks(ctx.channel(), 1))) {
            String remoteIp = sessionManager.getRemoteIp(session);
            long playerId = sessionManager.getPlayerId(session).longValue();
            LOGGER.error(String.format("In blacklist: [ip: %s, playerId: %d]", new Object[] {
                    remoteIp, Long.valueOf(playerId)}));
            sessionManager.closeSession(session);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel session = ctx.channel();
        if ((firewall.getClientType(session) != ClientType.MIS) && (firewall.isBlocked(session))) {
            String remoteIp = sessionManager.getRemoteIp(session);
            Long playerId = sessionManager.getPlayerId(session);
            LOGGER.error(String.format("In blacklist: [ip: %s, playerId: %d]", new Object[] {
                    remoteIp, playerId}));
            sessionManager.closeSession(session);
            return;
        }
        ctx.fireChannelActive();
    }

}
