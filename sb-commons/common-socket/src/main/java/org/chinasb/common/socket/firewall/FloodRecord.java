package org.chinasb.common.socket.firewall;

/**
 * 防火墙流量记录
 * 
 * @author zhujuan
 *
 */
public class FloodRecord {
    /**
     * 最近一次消息流量记录时间
     */
    private long lastSizeTime = 0L;
    /**
     * 最近一次消息数量记录时间
     */
    private long lastPackTime = 0L;
    /**
     * 最近一秒钟的消息数量
     */
    private int lastSecondPacks = 0;
    /**
     * 最近一秒钟的消息流量
     */
    private int lastSecondSizes = 0;
    /**
     * 最近一分钟的消息数量
     */
    private int lastMinutePacks = 0;
    /**
     * 最近一分钟的消息流量
     */
    private int lastMinuteSizes = 0;
    /**
     * 最近一次的校验错误时间
     */
    private long lastAuthCodeTime = 0L;
    /**
     * 最近一秒钟的校验错误次数
     */
    private int lastSecondAuthCodes = 0;
    /**
     * 最近一分钟的校验错误次数
     */
    private int lastMinuteAuthCodes = 0;

    public long getLastSizeTime() {
        return lastSizeTime;
    }

    public void setLastSizeTime(long lastSizeTime) {
        this.lastSizeTime = lastSizeTime;
    }

    public void addLastSizeTime(long lastSizeTime) {
        this.lastSizeTime += lastSizeTime;
    }

    public long getLastPackTime() {
        return lastPackTime;
    }

    public void setLastPackTime(long lastPackTime) {
        this.lastPackTime = lastPackTime;
    }

    public void addLastPackTime(long lastPackTime) {
        this.lastPackTime += lastPackTime;
    }

    public int getLastSecondPacks() {
        return lastSecondPacks;
    }

    public void setLastSecondPacks(int lastSecondPacks) {
        this.lastSecondPacks = lastSecondPacks;
    }

    public void addLastSecondPacks(int lastSecondPacks) {
        this.lastSecondPacks += lastSecondPacks;
    }

    public int getLastSecondSizes() {
        return lastSecondSizes;
    }

    public void setLastSecondSizes(int lastSecondSizes) {
        this.lastSecondSizes = lastSecondSizes;
    }

    public void addLastSecondSizes(int lastSecondSizes) {
        this.lastSecondSizes += lastSecondSizes;
    }

    public int getLastMinutePacks() {
        return lastMinutePacks;
    }

    public void setLastMinutePacks(int lastMinutePacks) {
        this.lastMinutePacks = lastMinutePacks;
    }

    public void addLastMinutePacks(int lastMinutePacks) {
        this.lastMinutePacks += lastMinutePacks;
    }

    public int getLastMinuteSizes() {
        return lastMinuteSizes;
    }

    public void setLastMinuteSizes(int lastMinuteSizes) {
        this.lastMinuteSizes = lastMinuteSizes;
    }

    public void addLastMinuteSizes(int lastMinuteSizes) {
        this.lastMinuteSizes += lastMinuteSizes;
    }

    public long getLastAuthCodeTime() {
        return lastAuthCodeTime;
    }

    public void setLastAuthCodeTime(long lastAuthCodeTime) {
        this.lastAuthCodeTime = lastAuthCodeTime;
    }

    public void addLastAuthCodeTime(long lastAuthCodeTime) {
        this.lastAuthCodeTime += lastAuthCodeTime;
    }

    public int getLastSecondAuthCodes() {
        return lastSecondAuthCodes;
    }

    public void setLastSecondAuthCodes(int lastSecondAuthCodes) {
        this.lastSecondAuthCodes = lastSecondAuthCodes;
    }

    public void addLastSecondAuthCodes(int lastSecondAuthCodes) {
        this.lastSecondAuthCodes += lastSecondAuthCodes;
    }

    public int getLastMinuteAuthCodes() {
        return lastMinuteAuthCodes;
    }

    public void setLastMinuteAuthCodes(int lastMinuteAuthCodes) {
        this.lastMinuteAuthCodes = lastMinuteAuthCodes;
    }

    public void addLastMinuteAuthCodes(int lastMinuteAuthCodes) {
        this.lastMinuteAuthCodes += lastMinuteAuthCodes;
    }

    public String toString() {
        return "FloodRecode [lastSizeTime=" + lastSizeTime + ", lastPackTime=" + lastPackTime
                + ", lastSecondPacks=" + lastSecondPacks + ", lastSecondSizes=" + lastSecondSizes
                + ", lastMinutePacks=" + lastMinutePacks + ", lastMinuteSizes=" + lastMinuteSizes
                + ", lastAuthCodeTime=" + lastAuthCodeTime + ", lastSecondAuthCodes="
                + lastSecondAuthCodes + ", lastMinuteAuthCodes=" + lastMinuteAuthCodes + "]";
    }
}
