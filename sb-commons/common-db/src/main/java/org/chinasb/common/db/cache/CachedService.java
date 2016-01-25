package org.chinasb.common.db.cache;

/**
 * 缓存服务接口
 * 
 * @author zhujuan
 */
public interface CachedService {
    /**
     * 添加实体缓存
     * 
     * @param key
     * @param value
     * @return
     */
    Object put2EntityCache(String key, Object value);

    /**
     * 添加实体缓存
     * 
     * @param key
     * @param value
     * @param timeToLive 存活时间
     * @return
     */
    Object put2EntityCache(String key, Object value, long timeToLive);

    /**
     * 获取实体缓存
     * 
     * @param key
     * @return
     */
    Object getFromEntityCache(String key);

    /**
     * 移除实体缓存
     * 
     * @param key
     */
    void removeFromEntityCache(String key);

    /**
     * 添加通用缓存（覆盖模式）
     * 
     * @param key
     * @param value
     */
    void put2CommonCache(String key, Object value);

    /**
     * 添加通用缓存（覆盖模式）
     * 
     * @param key
     * @param value
     * @param timeToLive 存活时间
     */
    void put2CommonCache(String key, Object value, long timeToLive);

    /**
     * 添加通用缓存（put-if-absent模式）
     * 
     * @param key
     * @param value
     * @return
     */
    Object put2CommonCacheIfAbsent(String key, Object value);

    /**
     * 添加通用缓存（put-if-absent模式）
     * 
     * @param key
     * @param value
     * @param timeToLive 存活时间
     * @return
     */
    Object put2CommonCacheIfAbsent(String key, Object value, long timeToLive);

    /**
     * 添加通用缓存（覆盖模式）
     * 
     * @param hashKey
     * @param subKey
     * @param value
     */
    void put2CommonHashCache(String hashKey, String subKey, Object value);

    /**
     * 添加通用缓存（覆盖模式）
     * 
     * @param hashKey
     * @param subKey
     * @param value
     * @param timeToLive 存活时间
     */
    void put2CommonHashCache(String hashKey, String subKey, Object value, long timeToLive);

    /**
     * 添加通用缓存（put-if-absent模式）
     * 
     * @param hashKey
     * @param subKey
     * @param value
     * @return
     */
    Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object value);

    /**
     * 添加通用缓存（put-if-absent模式）
     * 
     * @param hashKey
     * @param subKey
     * @param value
     * @param timeToLive 存活时间
     * @return
     */
    Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object value, long timeToLive);

    /**
     * 获取通用缓存
     * 
     * @param key
     * @return
     */
    Object getFromCommonCache(String key);

    /**
     * 获取通用缓存
     * 
     * @param hashKey
     * @param subKey
     * @return
     */
    Object getFromCommonHashCache(String hashKey, String subKey);

    /**
     * 移除通用缓存
     * 
     * @param key
     */
    void removeFromCommonCache(String key);

    /**
     * 移除通用缓存
     * 
     * @param hashKey
     * @param subKey
     */
    void removeFromCommonHashCache(String hashKey, String subKey);

    /**
     * 清除过期缓存
     * 
     * @param clearInValidCommonCache 是否清除通用缓存标志{true：清除, false:忽略}
     */
    void clearInValidateCacheObject(boolean clearInValidCommonCache);
}
