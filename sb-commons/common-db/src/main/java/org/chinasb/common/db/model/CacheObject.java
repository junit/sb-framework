package org.chinasb.common.db.model;

import org.chinasb.common.utility.TimeConstant;

/**
 * 缓存对象
 * @author zhujuan
 */
public class CacheObject {
    /**
     * 缓存实体
     */
    private Object entity;
    /**
     * 生存时间
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
     * 返回缓存对象
     * @param entity 缓存实体
     * @return
     */
    public static CacheObject valueOf(Object entity) {
        CacheObject entityCacheObject = new CacheObject();
        entityCacheObject.entity = entity;
        entityCacheObject.ttl = TimeConstant.ONE_DAY_MILLISECOND;
        entityCacheObject.createTime = System.currentTimeMillis();
        entityCacheObject.expireTime = (entityCacheObject.createTime + entityCacheObject.ttl);
        return entityCacheObject;
    }
    
    /**
     * 返回缓存对象
     * @param entity 缓存实体
     * @param timeToLive 存活时间
     * @return
     */
    public static CacheObject valueOf(Object entity, long timeToLive) {
        CacheObject entityCacheObject = new CacheObject();
        entityCacheObject.entity = entity;
        entityCacheObject.ttl = timeToLive;
        entityCacheObject.createTime = System.currentTimeMillis();
        entityCacheObject.expireTime = (entityCacheObject.createTime + entityCacheObject.ttl);
        return entityCacheObject;
    }

    /**
     * 缓存过期有效性检验
     * @return false: 过期; true: 未过期
     */
    public boolean isValidate() {
        return expireTime >= System.currentTimeMillis();
    }

    /**
     * 延长缓存的过期时间
     * @param addExpireTime
     * @param maxExpireTime
     */
    public void increaseExpireTime(int addExpireTime, int maxExpireTime) {
        if (expireTime < maxExpireTime + System.currentTimeMillis()) {
            expireTime += maxExpireTime;
        }
    }

    /**
     * 获得缓存实体
     * @return
     */
    public Object getEntity() {
        return entity;
    }

    /**
     * 获得存活时间
     * @return
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * 获得创建时间
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 获得过期时间
     * @return
     */
    public long getExpireTime() {
        return expireTime;
    }
}
