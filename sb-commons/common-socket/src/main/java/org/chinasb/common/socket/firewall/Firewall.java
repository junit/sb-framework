package org.chinasb.common.socket.firewall;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chinasb.common.socket.SessionManager;
import org.chinasb.common.socket.config.ServerConfig;
import org.chinasb.common.socket.type.SessionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

/**
 * 防火墙
 * 
 * @author zhujuan
 *
 */
@Component
public class Firewall {
    private static final Log LOGGER = LogFactory.getLog(Firewall.class);
    @Autowired
    private SessionManager sessionManager;
    /**
     * 每秒钟最大消息包数量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.packages.second")
    private Integer maxPacksPerSecond = Integer.valueOf(64);
    /**
     * 每分钟最大消息包数量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.packages.minute")
    private Integer maxPacksPerMinute = Integer.valueOf(1024);
    /**
     * 每秒钟最大消息包流量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.bytes.second")
    private Integer maxBytesPerSecond = Integer.valueOf(2048);
    /**
     * 每分钟最大消息包流量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.bytes.minute")
    private Integer maxBytesPerMinute = Integer.valueOf(65536);
    /**
     * 可疑活动行为侦测次数限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.block.detect.count")
    private Integer blockDetectCount = Integer.valueOf(3);
    /**
     * IP封锁时间（分钟）
     */
    @Autowired(required = false)
    @Qualifier("firewall.block.ip.minutes")
    private Integer blockIpMinutes = Integer.valueOf(5);
    /**
     * 用户封锁时间（分钟）
     */
    @Autowired(required = false)
    @Qualifier("firewall.block.user.minutes")
    private Integer blockUserMinutes = Integer.valueOf(5);
    /**
     * 客户端的活动数量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.clients.actives")
    private Integer maxClientsActives = Integer.valueOf(4000);
    /**
     * 客户端的连接数量限制
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.clients.limit")
    private Integer maxClientsLimit = Integer.valueOf(5000);
    /**
     * 每秒钟最大消息校验失败次数
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.authcode.errors.second")
    private Integer maxAuthCodeErrorsPerSecond = Integer.valueOf(64);
    /**
     * 每分钟最大消息校验失败次数
     */
    @Autowired(required = false)
    @Qualifier("firewall.max.authcode.errors.minute")
    private Integer maxAuthCodeErrorsPerMinute = Integer.valueOf(1024);
    /**
     * 客户端数量记录
     */
    private static final AtomicInteger CLIENTS = new AtomicInteger();
    /**
     * 封锁的IP列表
     */
    private static ConcurrentHashMap<String, Long> BLOCKED_IPS =
            new ConcurrentHashMap<String, Long>(1);
    /**
     * 封锁的玩家列表
     */
    private static ConcurrentHashMap<Long, Long> BLOCKED_PLAYER_IDS =
            new ConcurrentHashMap<Long, Long>(1);
    /**
     * 可疑IP列表
     */
    private static ConcurrentHashMap<String, AtomicInteger> SUSPICIOUS_IPS =
            new ConcurrentHashMap<String, AtomicInteger>(1);
    /**
     * 可疑玩家列表
     */
    private static ConcurrentHashMap<Long, AtomicInteger> SUSPICIOUS_PLAYERIDS =
            new ConcurrentHashMap<Long, AtomicInteger>(1);

    /**
     * 获取客户端数量
     * 
     * @return
     */
    public int getClients() {
        return CLIENTS.get();
    }

    /**
     * 客户端数量计数增加1
     * 
     * @return
     */
    public int increaseClients() {
        return CLIENTS.incrementAndGet();
    }

    /**
     * 客户端数量计数减少1
     * 
     * @return
     */
    public int decreaseClients() {
        return CLIENTS.decrementAndGet();
    }

    /**
     * 当前客户端是否超过连接数量限制
     * 
     * @param currClients
     * @return
     */
    public boolean isMaxClientLimit(int currClients) {
        return currClients > maxClientsLimit.intValue();
    }

    /**
     * 当前客户端是否超过活动数量限制
     * 
     * @param currClients
     * @return
     */
    public boolean isMaxClientActives(int currClients) {
        return currClients > maxClientsActives.intValue();
    }

    /**
     * 当前客户端是否超过活动数量限制
     * 
     * @return
     */
    public boolean isMaxClientActive() {
        return isMaxClientActives(getClients());
    }

    /**
     * 检测Session封锁状态
     * 
     * @param session
     * @return
     */
    public boolean isBlocked(Channel session) {
        return (isIpBlock(session)) || (isPlayerIdBlock(session));
    }

    /**
     * 检测Session的IP封锁状态
     * 
     * @param session
     * @return
     */
    private boolean isIpBlock(Channel session) {
        String remoteIp = sessionManager.getRemoteIp(session);
        if (Strings.isNullOrEmpty(remoteIp)) {
            return false;
        }
        Long blockedTime = (Long) BLOCKED_IPS.get(remoteIp);
        if (blockedTime == null) {
            return false;
        }
        if (blockedTime.longValue() <= System.currentTimeMillis()) {
            BLOCKED_IPS.remove(remoteIp);
            return false;
        }
        return true;
    }

    /**
     * 检测Session的用户封锁状态
     * 
     * @param session
     * @return
     */
    private boolean isPlayerIdBlock(Channel session) {
        long playerId = sessionManager.getPlayerId(session).longValue();
        Long blockedTime = (Long) BLOCKED_PLAYER_IDS.get(Long.valueOf(playerId));
        if (blockedTime == null) {
            return false;
        }
        if (blockedTime.longValue() <= System.currentTimeMillis()) {
            BLOCKED_PLAYER_IDS.remove(Long.valueOf(playerId));
            return false;
        }
        return true;
    }

    /**
     * 封锁IP
     * 
     * @param ip
     */
    public void blockIp(String ip) {
        long currentTimeMillis = System.currentTimeMillis();
        int blockIpMillis = getBlockIpMinutesOfMilliSecond();
        BLOCKED_IPS.put(Strings.isNullOrEmpty(ip) ? "" : ip,
                Long.valueOf(currentTimeMillis + blockIpMillis));
    }

    /**
     * 解锁IP
     * 
     * @param remoteIp
     */
    public void unblockIp(String remoteIp) {
        BLOCKED_IPS.remove(Strings.isNullOrEmpty(remoteIp) ? "" : remoteIp);
    }

    /**
     * 封锁玩家
     * 
     * @param playerId
     */
    public void blockPlayer(long playerId) {
        long currTime = System.currentTimeMillis();
        int blockUserTime = getBlockMinutesOfMilliSecond();
        BLOCKED_PLAYER_IDS.put(Long.valueOf(playerId), Long.valueOf(currTime + blockUserTime));
    }

    /**
     * 解锁玩家
     * 
     * @param playerId
     */
    public void unblockPlayer(long playerId) {
        BLOCKED_PLAYER_IDS.remove(Long.valueOf(playerId));
    }

    /**
     * 封锁类型
     * 
     * @author zhujuan
     *
     */
    static enum FirewallType {
        /**
         * 消息数量
         */
        PACK,
        /**
         * 消息流量
         */
        BYTE,
        /**
         * 校验错误次数
         */
        AUTH_CODE;
    }

    /**
     * 检测消息流量限制是否超标并达到封锁条件
     * 
     * @param session
     * @param bytes 本次消息流量的增量
     * @return
     */
    public boolean blockedByBytes(Channel session, int bytes) {
        return checkBlock(session, FirewallType.BYTE, bytes);
    }

    /**
     * 检测消息数量限制是否超标并达到封锁条件
     * 
     * @param session
     * @param packs 本次消息数量的增量
     * @return
     */
    public boolean blockedByPacks(Channel session, int packs) {
        return checkBlock(session, FirewallType.PACK, packs);
    }

    /**
     * 检测校验错误次数限制是否超标并达到封锁条件
     * 
     * @param session
     * @param errors 本次校验错误次数的增量
     * @return
     */
    public boolean blockedByAuthCodeErrors(Channel session, int errors) {
        return checkBlock(session, FirewallType.AUTH_CODE, errors);
    }

    /**
     * 检测防火墙类型限制是否超标并达到封锁条件
     * 
     * @param session
     * @param type 防火墙类型
     * @param amount 防火墙相关类型的数据增量
     * @return
     */
    private boolean checkBlock(Channel session, FirewallType type, int amount) {
        if (session == null) {
            return false;
        }
        if (isBlocked(session)) {
            return true;
        }
        if (amount <= 0) {
            return false;
        }
        FloodRecord floodCheck = session.attr(SessionType.FLOOD_RECORD_KEY).get();
        if (floodCheck == null) {
            session.attr(SessionType.FLOOD_RECORD_KEY).setIfAbsent(new FloodRecord());
            floodCheck = session.attr(SessionType.FLOOD_RECORD_KEY).get();
        }
        boolean suspicious = false;
        if (type == FirewallType.BYTE) {
            suspicious = avalidateWithBytes(amount, floodCheck);
        } else if (type == FirewallType.PACK) {
            suspicious = avalidateWithPackages(amount, floodCheck);
        } else if (type == FirewallType.AUTH_CODE) {
            suspicious = avalidateWithAuthcode(amount, floodCheck);
        }
        boolean isBlock = false;
        if (suspicious) {
            String remoteIp = sessionManager.getRemoteIp(session);
            Long playerId = sessionManager.getPlayerId(session);
            if (playerId.longValue() <= 0L) {
                AtomicInteger blocks = (AtomicInteger) SUSPICIOUS_IPS.get(remoteIp);
                if (blocks == null) {
                    SUSPICIOUS_IPS.putIfAbsent(remoteIp, new AtomicInteger());
                    blocks = (AtomicInteger) SUSPICIOUS_IPS.get(remoteIp);
                }
                if (blocks.incrementAndGet() >= blockDetectCount.intValue()) {
                    blocks.set(0);
                    isBlock = true;
                    blockIp(remoteIp);
                }
            } else {
                AtomicInteger blocks = (AtomicInteger) SUSPICIOUS_PLAYERIDS.get(playerId);
                if (blocks == null) {
                    SUSPICIOUS_PLAYERIDS.putIfAbsent(playerId, new AtomicInteger());
                    blocks = (AtomicInteger) SUSPICIOUS_PLAYERIDS.get(playerId);
                }
                if (blocks.incrementAndGet() >= blockDetectCount.intValue()) {
                    blocks.set(0);
                    isBlock = true;
                    blockPlayer(playerId.longValue());
                }
            }
            LOGGER.error(String.format("{%s}", new Object[] {floodCheck}));
            LOGGER.error(String.format(
                    "ip: %s, playerId: %d, block: %s",
                    new Object[] {remoteIp,
                            Long.valueOf(playerId == null ? 0L : playerId.longValue()),
                            String.valueOf(isBlock)}));
        }
        return isBlock;
    }

    /**
     * 删除可疑行为的侦测记录
     * 
     * @param session
     */
    public void removeBlockCounter(Channel session) {
        if (session != null) {
            try {
                SUSPICIOUS_IPS.remove(sessionManager.getRemoteIp(session));
                SUSPICIOUS_PLAYERIDS.remove(sessionManager.getPlayerId(session));
            } catch (Exception e) {
                LOGGER.error("删除Session的可疑记录异常", e);
            }
        }
    }

    /**
     * 检测校验码错误数次是否溢出
     * 
     * @param amount 本次数据增量
     * @param floodCheck
     * @return
     */
    private boolean avalidateWithAuthcode(int amount, FloodRecord floodCheck) {
        long currentMillis = System.currentTimeMillis();
        long currMinue = currentMillis / 60000L;
        long currSececond = currentMillis / 1000L;
        long lastSec = floodCheck.getLastAuthCodeTime() / 1000L;
        long lastMin = floodCheck.getLastAuthCodeTime() / 60000L;
        floodCheck.setLastAuthCodeTime(currentMillis);
        if (lastMin == currMinue) {
            floodCheck.addLastMinuteAuthCodes(amount);
            if (lastSec != currSececond) {
                floodCheck.setLastSecondAuthCodes(amount);
            } else {
                floodCheck.addLastSecondAuthCodes(amount);
            }
        } else {
            floodCheck.setLastMinuteAuthCodes(amount);
            floodCheck.setLastSecondAuthCodes(amount);
        }
        int lastMinuteAuthCodes = floodCheck.getLastMinuteAuthCodes();
        int lastSecondAuthCodes = floodCheck.getLastSecondAuthCodes();
        if (lastMinuteAuthCodes >= maxAuthCodeErrorsPerMinute.intValue()) {
            floodCheck.setLastMinuteAuthCodes(0);
            floodCheck.setLastSecondAuthCodes(0);
            LOGGER.error(String
                    .format("AuthCode errors overflow: lastMinuteAuthCodes[%d], maxAuthCodeErrorsPerMinute[%d]",
                            new Object[] {Integer.valueOf(lastMinuteAuthCodes),
                                    maxAuthCodeErrorsPerMinute}));
            return true;
        }
        if (lastSecondAuthCodes >= maxAuthCodeErrorsPerSecond.intValue()) {
            floodCheck.setLastMinuteAuthCodes(0);
            floodCheck.setLastSecondAuthCodes(0);
            LOGGER.error(String
                    .format("AuthCode errors overflow: lastSecondAuthCodes[%d], maxAuthCodeErrorsPerSecond[%d]",
                            new Object[] {Integer.valueOf(lastSecondAuthCodes),
                                    maxAuthCodeErrorsPerSecond}));
            return true;
        }
        return false;
    }

    /**
     * 检测消息数量是否溢出
     * 
     * @param amount 本次数据增量
     * @param floodCheck
     * @return
     */
    private boolean avalidateWithPackages(int amount, FloodRecord floodCheck) {
        long currentMillis = System.currentTimeMillis();
        long currMinue = currentMillis / 60L;
        long currSececond = currentMillis / 1000L;
        long lastMin = floodCheck.getLastPackTime() / 60L;
        long lastSec = floodCheck.getLastPackTime() / 1000L;
        floodCheck.setLastPackTime(currentMillis);
        if (lastMin == currMinue) {
            floodCheck.addLastMinutePacks(amount);
            if (lastSec != currSececond) {
                floodCheck.setLastSecondPacks(amount);
            } else {
                floodCheck.addLastSecondPacks(amount);
            }
        } else {
            floodCheck.setLastMinutePacks(amount);
            floodCheck.setLastSecondPacks(amount);
        }
        int lastMinutePacks = floodCheck.getLastMinutePacks();
        int lastSecondPacks = floodCheck.getLastSecondPacks();
        if (lastMinutePacks >= maxPacksPerMinute.intValue()) {
            floodCheck.setLastMinutePacks(0);
            floodCheck.setLastSecondPacks(0);
            LOGGER.error(String.format(
                    "Packs overflow: lastMinutePacks[%d], maxPacksPerMinute[%d]", new Object[] {
                            Integer.valueOf(lastMinutePacks), maxPacksPerMinute}));
            return true;
        }
        if (lastSecondPacks >= maxPacksPerSecond.intValue()) {
            floodCheck.setLastMinutePacks(0);
            floodCheck.setLastSecondPacks(0);
            LOGGER.error(String.format(
                    "Packs overflow: lastSecondPacks[%d], maxPacksPerSecond[%d]", new Object[] {
                            Integer.valueOf(lastSecondPacks), maxPacksPerSecond}));
            return true;
        }
        return false;
    }

    /**
     * 检测消息流量是否溢出
     * 
     * @param amount 本次数据增量
     * @param floodCheck
     * @return
     */
    private boolean avalidateWithBytes(int amount, FloodRecord floodCheck) {
        long currentMillis = System.currentTimeMillis();
        long currMinue = currentMillis / 60000L;
        long currSececond = currentMillis / 1000L;
        long lastSecond = floodCheck.getLastSizeTime() / 1000L;
        long lastMinue = floodCheck.getLastSizeTime() / 60000L;
        floodCheck.setLastSizeTime(currentMillis);
        if (lastMinue == currMinue) {
            floodCheck.addLastMinuteSizes(amount);
            if (lastSecond != currSececond) {
                floodCheck.setLastSecondSizes(amount);
            } else {
                floodCheck.addLastSecondSizes(amount);
            }
        } else {
            floodCheck.setLastMinuteSizes(amount);
            floodCheck.setLastSecondSizes(amount);
        }
        int lastMinuteSizes = floodCheck.getLastMinuteSizes();
        int lastSecondSizes = floodCheck.getLastSecondSizes();
        if (lastMinuteSizes >= maxBytesPerMinute.intValue()) {
            floodCheck.setLastMinuteSizes(0);
            floodCheck.setLastSecondSizes(0);
            LOGGER.error(String.format(
                    "Bytes overflow: lastMinuteSizes[%d], maxBytesPerMinute[%d]", new Object[] {
                            Integer.valueOf(lastMinuteSizes), maxBytesPerMinute}));
            return true;
        }
        if (lastSecondSizes >= maxBytesPerSecond.intValue()) {
            floodCheck.setLastMinuteSizes(0);
            floodCheck.setLastSecondSizes(0);
            LOGGER.error(String.format(
                    "Bytes overflow: lastSecondSizes[%d], maxBytesPerSecond[%d]", new Object[] {
                            Integer.valueOf(lastSecondSizes), maxBytesPerSecond}));
            return true;
        }
        return false;
    }

    /**
     * 获取客户端连接类型
     * 
     * @param session
     * @return
     */
    public ClientType getClientType(Channel session) {
        ClientType clientType = session.attr(SessionType.CLIENT_TYPE_KEY).get();
        if (clientType == null) {
            clientType = ClientType.ANONYMOUS;
            String remoteIp = sessionManager.getRemoteIp(session);
            if ((!Strings.isNullOrEmpty(remoteIp)) && (ServerConfig.isAllowMisIp(remoteIp))) {
                clientType = ClientType.MIS;
            }
        }
        if (clientType == ClientType.ANONYMOUS) {
            Long playerId = sessionManager.getPlayerId(session);
            if ((playerId != null) && (playerId.longValue() > 0L)) {
                clientType = ClientType.LOGIN_USER;
            }
        }
        session.attr(SessionType.CLIENT_TYPE_KEY).set(clientType);
        return clientType;
    }

    /**
     * 获取每秒钟最大消息包数量限制
     * 
     * @return
     */
    public int getMaxPacksPerSecond() {
        return maxPacksPerSecond.intValue();
    }

    /**
     * 获取每分钟最大消息包数量限制
     * 
     * @return
     */
    public int getMaxPacksPerMinute() {
        return maxPacksPerMinute.intValue();
    }

    /**
     * 获取每秒钟最大消息包流量限制
     * 
     * @return
     */
    public int getMaxBytesPerSecond() {
        return maxBytesPerSecond.intValue();
    }

    /**
     * 获取每分钟最大消息包流量限制
     * 
     * @return
     */
    public int getMaxBytesPerMinute() {
        return maxBytesPerMinute.intValue();
    }

    /**
     * 获取每秒钟最大消息校验失败次数
     * 
     * @return
     */
    public int getMaxAuthCodeErrorsPerSecond() {
        return maxAuthCodeErrorsPerSecond.intValue();
    }

    /**
     * 获取每分钟最大消息校验失败次数
     * 
     * @return
     */
    public int getMaxAuthCodeErrorsPerMinute() {
        return maxAuthCodeErrorsPerMinute.intValue();
    }

    /**
     * 获取可疑活动行为侦测次数限制
     * 
     * @return
     */
    public int getBlockDetectCount() {
        return blockDetectCount.intValue();
    }

    /**
     * 获取封锁IP时间(毫秒)
     * 
     * @return
     */
    public int getBlockIpMinutesOfMilliSecond() {
        return blockIpMinutes.intValue() * 60000;
    }

    /**
     * 获取封锁IP时间(秒)
     * 
     * @return
     */
    public int getBlockMinutesOfMilliSecond() {
        return blockUserMinutes.intValue() * 60000;
    }

    /**
     * 获取客户端连接数量限制
     * 
     * @return
     */
    public int getMaxClientsLimit() {
        return maxClientsLimit.intValue();
    }

    /**
     * 获取客户端活动数量限制
     * 
     * @return
     */
    public int getMaxClientsActives() {
        return maxClientsActives.intValue();
    }

    @PostConstruct
    protected void initialize() {
        LOGGER.error(String.format("防火墙允许的活动客户端数量: %d", new Object[] {maxClientsActives}));
    }
}
