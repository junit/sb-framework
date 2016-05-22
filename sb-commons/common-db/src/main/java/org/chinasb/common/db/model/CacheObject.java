package org.chinasb.common.db.model;

import org.chinasb.common.utility.Constants;

/**
 * 缓存对象
 * 
 * @author zhujuan
 */
public class CacheObject {
    /**
     * 缓存对象
     */
    private Object value;
    /**
     * 存活时间
     */
    private long ttl = 0L;
    /**
     * 创建时间
     */
    private long createTime = System.currentTimeMillis();
    /**
     * 过期时间
     */
    private long expireTime = createTime + ttl;

    /**
     * 将普通对象转换为缓存对象
     * 
     * @param value 普通对象
     * @return {@link CacheObject}
     */
    public static CacheObject valueOf(Object value) {
        CacheObject cacheObject = new CacheObject();
        cacheObject.value = value;
        cacheObject.ttl = Constants.ONE_DAY_MILLISECOND;
        cacheObject.createTime = System.currentTimeMillis();
        cacheObject.expireTime = (cacheObject.createTime + cacheObject.ttl);
        return cacheObject;
    }

    /**
     * 将普通对象转换为缓存对象
     * 
     * @param value 普通对象
     * @param timeToLive 存活时间
     * @return {@link CacheObject}
     */
    public static CacheObject valueOf(Object value, long timeToLive) {
        CacheObject cacheObject = new CacheObject();
        cacheObject.value = value;
        cacheObject.ttl = timeToLive;
        cacheObject.createTime = System.currentTimeMillis();
        cacheObject.expireTime = (cacheObject.createTime + cacheObject.ttl);
        return cacheObject;
    }

    /**
     * 检查缓存对象的有效性
     * 
     * @return true: 有效; false: 无效
     */
    public boolean isValidate() {
        return expireTime >= System.currentTimeMillis();
    }

    /**
     * 延长缓存对象的过期时间
     * 
     * @param addExpireTime
     */
    public void increaseExpireTime(int addExpireTime) {
        if (expireTime < (addExpireTime + System.currentTimeMillis())) {
            expireTime += addExpireTime;
        }
    }

    /**
     * 获取缓存的实体
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * 获取缓存的存活时间
     * 
     * @return
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * 获取缓存的创建时间
     * 
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 获取缓存的过期时间
     * 
     * @return
     */
    public long getExpireTime() {
        return expireTime;
    }
}
