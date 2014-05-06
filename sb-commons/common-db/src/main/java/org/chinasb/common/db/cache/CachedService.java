package org.chinasb.common.db.cache;

/**
 * 缓存管理服务
 * @author zhujuan
 */
public interface CachedService {
    /**
     * 增加实体对象到缓存
     * @param key 主键
     * @param object 缓存对象
     * @return
     */
    public Object put2EntityCache(String key, Object object);

    /**
     * 增加实体对象到缓存
     * @param key 主键
     * @param object 缓存对象
     * @param timeToLive 存活时间
     * @return
     */
    public Object put2EntityCache(String key, Object object, long timeToLive);

    /**
     * 获取缓存中的实体对象
     * @param key 主键
     * @return
     */
    public Object getFromEntityCache(String key);

    /**
     * 移除缓存中的实体对象
     * @param key 主键
     */
    public void removeFromEntityCache(String key);

    /**
     * 增加对象到通用缓存（覆盖模式）
     * @param key 主键
     * @param object 缓存对象
     */
    public void put2CommonCache(String key, Object object);

    /**
     * 增加对象到通用缓存（覆盖模式）
     * @param key 主键
     * @param object 缓存对象
     * @param timeToLive 存活时间
     */
    public void put2CommonCache(String key, Object object, long timeToLive);
    
    /**
     * 增加对象到通用公共缓存（put-if-absent模式）
     * @param key 主键
     * @param object 缓存对象
     * @return
     */
    public Object put2CommonCacheIfAbsent(String key, Object object);

    /**
     * 增加对象到通用缓存（put-if-absent模式）
     * @param key 主键
     * @param object 缓存对象
     * @param timeToLive 存活时间
     * @return
     */
    public Object put2CommonCacheIfAbsent(String key, Object object, long timeToLive);

    /**
     * 增加对象到通用缓存（覆盖模式）
     * @param hashKey
     * @param subKey
     * @param object
     */
    public void put2CommonHashCache(String hashKey, String subKey, Object object);

    /**
     * 增加对象到通用缓存（覆盖模式）
     * @param hashKey
     * @param subKey
     * @param object
     * @param timeToLive
     */
    public void put2CommonHashCache(String hashKey, String subKey, Object object, long timeToLive);

    /**
     * 增加对象到通用缓存（put-if-absent模式）
     * @param hashKey
     * @param subKey
     * @param object
     * @return
     */
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object object);

    /**
     * 增加对象到通用缓存（put-if-absent模式）
     * @param hashKey
     * @param subKey
     * @param object
     * @param timeToLive
     * @return
     */
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object object,
            long timeToLive);

    /**
     * 获得通用缓存中的对象
     * @param key 主键
     * @return
     */
    public Object getFromCommonCache(String key);

    /**
     * 获得通用缓存中的对象
     * @param hashKey 主键
     * @param subKey 子键
     * @return
     */
    public Object getFromCommonHashCache(String hashKey, String subKey);

    /**
     * 移除通用缓存中的对象
     * @param key 主键
     */
    public void removeFromCommonCache(String key);

    /**
     * 移除通用缓存中的对象
     * @param hashKey 主键
     * @param subKey 子键
     */
    public void removeFromCommonHashCache(String hashKey, String subKey);

    /**
     * 清除过期缓存
     * @param clearInValidCommonCache 清除通用缓存标志{true：清除, false:忽略}
     */
    public void clearValidateCacheObject(boolean clearInValidCommonCache);
}
