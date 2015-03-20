package org.chinasb.common.socket.type;

import io.netty.util.AttributeKey;

import org.chinasb.common.socket.codec.CodecContext;
import org.chinasb.common.socket.context.ApplicationContext;
import org.chinasb.common.socket.firewall.ClientType;
import org.chinasb.common.socket.firewall.FloodRecord;

/**
 * 会话存储相关数据类型
 * @author zhujuan
 *
 */
public interface SessionType {
    /**
     * 应用程序上下文信息
     */
    public static final AttributeKey<ApplicationContext> APPLICATION_CONTEXT_KEY = AttributeKey.valueOf("applicationContext");
    /**
     * 解码器上下文信息
     */
    public static final AttributeKey<CodecContext> CODEC_CONTEXT_KEY = AttributeKey.valueOf("codecContext");
    /**
     * 连接首次请求状态
     */
    public static final AttributeKey<Boolean> FIRST_REQUEST_KEY = AttributeKey.valueOf("firstRequest");
    /**
     * 玩家ID
     */
    public static final AttributeKey<Long> PLAYER_ID_KEY = AttributeKey.valueOf("playerId");
    /**
     * 客户端IP地址
     */
    public static final AttributeKey<String> REMOTE_HOST_KEY = AttributeKey.valueOf("remoteHost");
    /**
     * 防火墙流量记录
     */
    public static final AttributeKey<FloodRecord> FLOOD_RECORD_KEY = AttributeKey.valueOf("floodRecordKey");
    /**
     * 客户端连接类型
     */
    public static final AttributeKey<ClientType> CLIENT_TYPE_KEY = AttributeKey.valueOf("CLIENT_TYPE_KEY");
    /**
     * 客户端当前连接数量
     */
    public static final AttributeKey<Integer> WHICH_CLIENTS = AttributeKey.valueOf("whichClients");

}
