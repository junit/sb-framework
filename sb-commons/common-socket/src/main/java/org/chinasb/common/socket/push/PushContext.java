package org.chinasb.common.socket.push;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

import org.chinasb.common.socket.message.Response;

/**
 * 消息推送上下文
 * 
 * @author zhujuan
 *
 */
public class PushContext {
    private long pusherId;
    private Response response;
    private PushType type = PushType.ALL;
    private Collection<Long> playerIdList = null;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public PushType getType() {
        return type;
    }

    public void setType(PushType type) {
        this.type = type;
    }

    public Collection<Long> getPlayerIdList() {
        return playerIdList;
    }

    public void setPlayerIdList(Collection<Long> playerIdList) {
        this.playerIdList = playerIdList;
    }

    public long getPusherId() {
        return pusherId;
    }

    private static final AtomicLong automicId = new AtomicLong();

    private static long getAtomicId() {
        if (automicId.get() >= Long.MAX_VALUE) {
            automicId.set(1L);
        }
        return automicId.getAndIncrement();
    }

    /**
     * 返回一个推送至所有在线玩家的消息上下文对象
     * 
     * @param response 消息内容
     * @return
     */
    public static PushContext push2AllOnline(Response response) {
        PushContext context = new PushContext();
        context.type = PushType.ALL;
        context.response = response;
        context.pusherId = getAtomicId();
        return context;
    }

    /**
     * 返回一个推送至指定玩家的消息上下文对象
     * 
     * @param playerId 玩家
     * @param response 消息内容
     * @return
     */
    public static PushContext push2Player(long playerId, Response response) {
        PushContext context = new PushContext();
        context.pusherId = playerId;
        context.response = response;
        context.type = PushType.GROUP;
        context.playerIdList = Arrays.asList(new Long[] {Long.valueOf(playerId)});
        return context;
    }

    /**
     * 返回一个推送至指定玩家的消息上下文对象
     * 
     * @param playerIdList 玩家列表
     * @param response 消息内容
     * @return
     */
    public static PushContext push2Players(Collection<Long> playerIdList, Response response) {
        PushContext context = new PushContext();
        context.response = response;
        context.type = PushType.GROUP;
        context.pusherId = getAtomicId();
        if (playerIdList != null) {
            context.playerIdList = new HashSet<Long>(playerIdList);
        }
        return context;
    }
}
