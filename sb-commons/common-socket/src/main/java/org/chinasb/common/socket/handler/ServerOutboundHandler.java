package org.chinasb.common.socket.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.message.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * ServerOutboundHandler
 * 
 * @author zhujuan
 *
 */
public class ServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Log LOGGER = LogFactory.getLog(ServerOutboundHandler.class);

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
