package org.chinasb.common.socket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.chinasb.common.socket.codec.ResponseEncoder;
import org.chinasb.common.socket.message.Response;
import org.chinasb.common.socket.type.SessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

/**
 * 会话管理
 * 
 * @author zhujuan
 *
 */
@Component
public class SessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    /**
     * 管理后台会话列表
     */
    private static final ConcurrentHashMap<ChannelId, Channel> MIS_SESSION_MAP =
            new ConcurrentHashMap<ChannelId, Channel>();
    /**
     * 匿名用户列表
     */
    private static final ConcurrentHashMap<ChannelId, Channel> ANONYMOUS_SESSION_MAP =
            new ConcurrentHashMap<ChannelId, Channel>();
    /**
     * 在线用户列表
     */
    private static final ConcurrentHashMap<Long, Channel> ONLINE_PLAYERID_SESSION_MAP =
            new ConcurrentHashMap<Long, Channel>();
    /**
     * 历史最大在线用户数量
     */
    private int maxOnlineCount = 0;
    /**
     * 历史最小在线用户数量
     */
    private int minOnlineCount = 0;

    @Autowired
    @Qualifier("responseEncoder")
    private ResponseEncoder encoder;

    /**
     * 设置消息编码器
     * 
     * @param encoder
     */
    public void setEncoder(ResponseEncoder encoder) {
        if (encoder == null) {
            throw new NullPointerException("ResponseEncoder is null");
        }
        this.encoder = encoder;
    }

    /**
     * 获取玩家ID
     * 
     * @param session
     * @return
     */
    public Long getPlayerId(Channel session) {
        Long playerId = null;
        if (session != null) {
            playerId = session.attr(SessionType.PLAYER_ID_KEY).get();
        }
        return Long.valueOf(playerId == null ? 0L : playerId.longValue());
    }

    /**
     * 把玩家加入在线用户列表
     * 
     * @param playerId
     * @param session
     */
    public void put2OnlineList(long playerId, Channel session) {
        if (session == null) {
            return;
        }
        ChannelId sessionId = session.id();
        if (ANONYMOUS_SESSION_MAP.containsKey(sessionId)) {
            ANONYMOUS_SESSION_MAP.remove(sessionId);
        }
        Channel orignSession = ONLINE_PLAYERID_SESSION_MAP.put(Long.valueOf(playerId), session);
        if ((orignSession != null) && (orignSession.isActive())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("玩家[%d]加入在线用户列表前关闭之前的Session",
                        new Object[] {Long.valueOf(playerId)}));
            }
            orignSession.close();
        }
        session.attr(SessionType.PLAYER_ID_KEY).set(Long.valueOf(playerId));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("SessionId: [ %s ] 绑定角色ID:[ %d ] ", new Object[] {sessionId,
                    Long.valueOf(playerId)}));
        }
        int onlineCount = getCurrentOnlineCount();
        if (maxOnlineCount < onlineCount) {
            maxOnlineCount = onlineCount;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("把玩家[%d]加入在线用户列表", new Object[] {Long.valueOf(playerId)}));
        }
    }

    /**
     * 把玩家从在线用户列表删除
     * 
     * @param playerId
     */
    public void removeFromOnlineList(long playerId) {
        Channel session = ONLINE_PLAYERID_SESSION_MAP.remove(Long.valueOf(playerId));
        if (session == null) {
            return;
        }
        if (session.isActive()) {
            ANONYMOUS_SESSION_MAP.put(session.id(), session);
        }
        int onlineCount = getCurrentOnlineCount();
        if (minOnlineCount > onlineCount) {
            minOnlineCount = onlineCount;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("把玩家[%d]从在线用户列表删除", new Object[] {Long.valueOf(playerId)}));
        }
    }

    /**
     * 获取历史最大在线用户数量
     * 
     * @return
     */
    public int getMaxOnlineCount() {
        return maxOnlineCount;
    }

    /**
     * 设置历史最大在线用户数量
     * 
     * @param maxOnlineCount
     */
    public void setMaxOnlineCount(int maxOnlineCount) {
        this.maxOnlineCount = maxOnlineCount;
    }

    /**
     * 获取历史最小在线用户数量
     * 
     * @return
     */
    public int getMinOnlineCount() {
        return minOnlineCount;
    }

    /**
     * 设置历史最小在线用户数量
     * 
     * @param minOnlineCount
     */
    public void setMinOnlineCount(int minOnlineCount) {
        this.minOnlineCount = minOnlineCount;
    }

    /**
     * 重置历史用户在线记录
     */
    public void resetOnlineUserCount() {
        int onlineCount = getCurrentOnlineCount();
        maxOnlineCount = onlineCount;
        minOnlineCount = onlineCount;
    }

    /**
     * 把玩家从在线用户列表删除
     * 
     * @param session
     */
    public void removeFromOnlineList(Channel session) {
        Long playerId = getPlayerId(session);
        Channel oldSession = getSession(playerId.longValue());
        if ((oldSession != null) && (session == oldSession)) {
            removeFromOnlineList(playerId.longValue());
        }
    }

    /**
     * 把Session加入匿名用户列表
     * 
     * @param session
     */
    public void put2AnonymousList(Channel session) {
        if (session != null) {
            ChannelId sessionId = session.id();
            ANONYMOUS_SESSION_MAP.put(sessionId, session);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Session [%s]加入匿名用户列表", new Object[] {sessionId}));
            }
        }
    }

    /**
     * 把Session从匿名用户列表删除
     * 
     * @param session
     */
    public void removeFromAnonymousList(Channel session) {
        if (session != null) {
            ChannelId sessionId = session.id();
            if (ANONYMOUS_SESSION_MAP.containsKey(sessionId)) {
                ANONYMOUS_SESSION_MAP.remove(sessionId);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Session [%s]从匿名用户列表删除", new Object[] {sessionId}));
                }
            }
        }
    }

    /**
     * 检测玩家是否在线
     * 
     * @param playerId
     * @return
     */
    public boolean isOnline(long playerId) {
        return ONLINE_PLAYERID_SESSION_MAP.containsKey(Long.valueOf(playerId));
    }

    /**
     * 获取所有在线玩家ID列表
     * 
     * @return
     */
    public Set<Long> getOnlinePlayerIdList() {
        Set<Long> onLinePlayerIdList = new HashSet<Long>();
        Set<Long> onlinePlayerIds = ONLINE_PLAYERID_SESSION_MAP.keySet();
        if ((onlinePlayerIds != null) && (!onlinePlayerIds.isEmpty())) {
            onLinePlayerIdList.addAll(onlinePlayerIds);
        }
        return onLinePlayerIdList;
    }

    /**
     * 获取当前在线玩家数量
     * 
     * @return
     */
    public int getCurrentOnlineCount() {
        return ONLINE_PLAYERID_SESSION_MAP.size();
    }

    /**
     * 获取玩家Session
     * 
     * @param playerId
     * @return
     */
    public Channel getSession(long playerId) {
        return ONLINE_PLAYERID_SESSION_MAP.get(Long.valueOf(playerId));
    }

    /**
     * 获取所有管理Session
     * 
     * @return
     */
    public List<Channel> getMisSessions() {
        return new ArrayList<Channel>(MIS_SESSION_MAP.values());
    }

    /**
     * 获取所有匿名Session
     * 
     * @return
     */
    public List<Channel> getAnonymousSessions() {
        return new ArrayList<Channel>(ANONYMOUS_SESSION_MAP.values());
    }

    /**
     * 把Session放入到管理后台会话列表
     * 
     * @param session
     */
    public void put2MisList(Channel session) {
        if (session != null) {
            ChannelId sessionId = session.id();
            if (ANONYMOUS_SESSION_MAP.containsKey(sessionId)) {
                ANONYMOUS_SESSION_MAP.remove(sessionId);
            }
            MIS_SESSION_MAP.put(sessionId, session);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Session [%s]放入到管理后台会话列表", new Object[] {sessionId}));
            }
        }
    }

    /**
     * 把Session从管理后台会话列表删除
     * 
     * @param session
     */
    public void removeFromMisList(Channel session) {
        if (session != null) {
            ChannelId sessionId = session.id();
            if (MIS_SESSION_MAP.containsKey(sessionId)) {
                MIS_SESSION_MAP.remove(sessionId);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Session [%s]从管理后台会话列表删除", new Object[] {sessionId}));
                }
            }
            if (session.isActive()) {
                ANONYMOUS_SESSION_MAP.put(sessionId, session);
            }
        }
    }

    /**
     * 发送消息
     * 
     * @param playerId 玩家ID
     * @param response 消息
     */
    public void write(long playerId, Response response) {
        write(getSession(playerId), response);
    }

    /**
     * 发送消息
     * 
     * @param session
     * @param response
     */
    public void write(Channel session, Response response) {
        if (session == null) {
            return;
        }
        Long playerId = getPlayerId(session);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("写数据 [playerId: %d]", new Object[] {playerId}));
        }
        if (session.isActive()) {
            if (response != null) {
                session.writeAndFlush(response);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("写数据 给玩家[playerId: %d], 因Session未连接, 从在线列表删除",
                        new Object[] {playerId}));
            }
            closeSession(session);
        }
    }

    /**
     * 发送消息
     * 
     * @param playerIdList 玩家ID列表
     * @param response 消息
     */
    public void write(Collection<Long> playerIdList, Response response) {
        if ((playerIdList == null) || (playerIdList.isEmpty())) {
            return;
        }
        byte[] bytes = encoder.encodeResponse(response);
        if (bytes == null) {
            return;
        }
        for (Iterator<Long> it = playerIdList.iterator(); it.hasNext();) {
            long playerId = ((Long) it.next()).longValue();
            Channel session = getSession(playerId);
            if (session != null) {
                ByteBuf byteBuf = encoder.transformByteArray(bytes);
                if (byteBuf != null) {
                    write(session, byteBuf);
                }
            }
        }
    }

    /**
     * 向全部在线玩家发送消息
     * 
     * @param response
     */
    public void writeAllOnline(Response response) {
        write(new HashSet<Long>(ONLINE_PLAYERID_SESSION_MAP.keySet()), response);
    }

    /**
     * 发送消息
     * 
     * @param playerId
     * @param buffer
     */
    public void write(long playerId, ByteBuf buffer) {
        if (buffer != null) {
            write(getSession(playerId), buffer);
        }
    }

    /**
     * 发送消息
     * 
     * @param session
     * @param buffer
     */
    public void write(Channel session, ByteBuf buffer) {
        if (session == null) {
            return;
        }
        Long playerId = getPlayerId(session);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("写数据 [playerId: %d]", new Object[] {playerId}));
        }
        if (session.isActive()) {
            if (buffer != null) {
                session.writeAndFlush(buffer);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("写数据 给玩家[playerId: %d], 因Session未连接, 从在线列表删除",
                        new Object[] {playerId}));
            }
            closeSession(session);
        }
    }

    /**
     * 关闭Session连接
     * 
     * @param session
     */
    public void closeSession(Channel session) {
        if (session != null) {
            session.close();
        }
    }

    /**
     * 获取Session的IP地址
     * 
     * @param session
     * @return
     */
    public String getRemoteIp(Channel session) {
        if (session == null) {
            return "";
        }
        String remoteIp = session.attr(SessionType.REMOTE_HOST_KEY).get();
        if (!Strings.isNullOrEmpty(remoteIp)) {
            return remoteIp;
        }
        try {
            remoteIp = ((InetSocketAddress) session.remoteAddress()).getAddress().getHostAddress();
            if (Strings.isNullOrEmpty(remoteIp)) {
                remoteIp =
                        ((InetSocketAddress) session.localAddress()).getAddress().getHostAddress();
            }
            session.attr(SessionType.REMOTE_HOST_KEY).set(remoteIp);;
        } catch (Exception e) {
            remoteIp = null;
        }
        return Strings.isNullOrEmpty(remoteIp) ? "" : remoteIp;
    }
}
