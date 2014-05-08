package org.chinasb.common.db.cache.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.chinasb.common.db.cache.CachedService;
import org.chinasb.common.db.executor.DbService;
import org.chinasb.common.db.model.CacheObject;
import org.chinasb.common.utility.TimeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;

/**
 * 缓存管理服务
 * @author zhujuan
 */
@Service
public class CacheServiceImpl implements CachedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);
    /**
     * 通用对象缓存容量
     */
    @Autowired(required = false)
    @Qualifier("dbcache.max_capacity_of_common_cache")
    private Integer commonCacheSize;
    /**
     * 实体对象缓存容量
     */
    @Autowired(required = false)
    @Qualifier("dbcache.max_capacity_of_entity_cache")
    private Integer entityCacheSize;
    /**
     * 实体对象缓存默认过期时间
     */
    @Autowired(required = false)
    @Qualifier("dbcache.ttl_of_entity_cache")
    private Integer entityCacheTTL;

    @Autowired
    private DbService dbService;
    private static int ONE_MIN_MILISECONDS = TimeConstant.ONE_MINUTE_MILLISECOND;
    /**
     * 最大延长时间
     */
    private static int MAX_EXTEND_MILISECONDS = TimeConstant.ONE_MINUTE_MILLISECOND * 10;
    /**
     * 通用对象缓存
     */
    private Cache<String, Object> COMMON_CACHE;
    /**
     * 实体对象缓存
     */
    private Cache<String, CacheObject> ENTITY_CACHE;

    /**
     * 构造器
     */
    public CacheServiceImpl() {
        commonCacheSize = Integer.valueOf(500000);
        entityCacheSize = Integer.valueOf(500000);
        entityCacheTTL = Integer.valueOf(ONE_MIN_MILISECONDS * 120);
        COMMON_CACHE = null;
        ENTITY_CACHE = null;
    }
    
    @PostConstruct
    protected void initialize() {
        COMMON_CACHE = CacheBuilder.newBuilder().maximumSize(commonCacheSize.intValue()).build();
        ENTITY_CACHE = CacheBuilder.newBuilder().maximumSize(entityCacheSize.intValue()).build();
    }

    @Override
    public Object put2EntityCache(String key, Object object) {
        return put2EntityCache(key, object, -1L);
    }

    @Override
    public Object put2EntityCache(String key, Object object, long timeToLive) {
        if ((key == null) || (object == null)) {
            return object;
        }
        if (timeToLive > 0L) {
            ENTITY_CACHE.asMap().putIfAbsent(key, CacheObject.valueOf(object, timeToLive));
        } else {
            ENTITY_CACHE.asMap().putIfAbsent(key,
                    CacheObject.valueOf(object, entityCacheTTL.intValue()));
        }
        return getFromEntityCache(key);
    }

    @Override
    public Object getFromEntityCache(String key) {
        CacheObject cacheObject = (CacheObject) ENTITY_CACHE.asMap().get(key);
        if (cacheObject == null) {
            return null;
        }
        if (!cacheObject.isValidate()) {
            synchronized (cacheObject) {
                cacheObject = (CacheObject) ENTITY_CACHE.asMap().get(key);
                if (cacheObject == null) {
                    return null;
                }
                if (!cacheObject.isValidate()) {
                    Object entity = cacheObject.getEntity();
                    if (!dbService.isInDbQueue(entity)) {
                        ENTITY_CACHE.asMap().remove(key);
                        return null;
                    }
                    cacheObject.increaseExpireTime(ONE_MIN_MILISECONDS, MAX_EXTEND_MILISECONDS);
                }
                return cacheObject.getEntity();
            }
        }
        cacheObject.increaseExpireTime(ONE_MIN_MILISECONDS, MAX_EXTEND_MILISECONDS);
        return cacheObject.getEntity();
    }

    @Override
    public void removeFromEntityCache(String key) {
        if (ENTITY_CACHE.asMap().containsKey(key)) {
            ENTITY_CACHE.asMap().remove(key);
        }
    }

    @Override
    public void put2CommonCache(String key, Object object) {
        put2CommonCache(key, object, -1L);
    }

    @Override
    public void put2CommonCache(String key, Object object, long timeToLive) {
        if (key == null || object == null) {
            return;
        }
        if (timeToLive > 0L) {
            COMMON_CACHE.put(key, CacheObject.valueOf(object, timeToLive));
        } else {
            COMMON_CACHE.put(key, object);
        }
    }
    
    @Override
    public Object put2CommonCacheIfAbsent(String key, Object object) {
        return put2CommonCacheIfAbsent(key, object, -1L);
    }

    @Override
    public Object put2CommonCacheIfAbsent(String key, Object object, long timeToLive) {
        if (key == null || object == null) {
            return object;
        }
        if (timeToLive > 0L) {
            CacheObject cacheObject =
                    (CacheObject) COMMON_CACHE.asMap().putIfAbsent(key,
                            CacheObject.valueOf(object, timeToLive));
            if(cacheObject == null) {
                return null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "CacheObject- [createTime: [{}] , ttl: [{}] , isValidate: [{}] ]",
                        new Object[] {Long.valueOf(cacheObject.getCreateTime()),
                                Long.valueOf(cacheObject.getTtl()),
                                Boolean.valueOf(cacheObject.isValidate())});
            }
            if (!cacheObject.isValidate()) {
                if (COMMON_CACHE.asMap().containsKey(key)) {
                    COMMON_CACHE.asMap().remove(key);
                }
                return null;
            }
            return cacheObject.getEntity();
        }
        return COMMON_CACHE.asMap().putIfAbsent(key, object);
    }

    @Override
    public void put2CommonHashCache(String hashKey, String subKey, Object object) {
        put2CommonHashCache(hashKey, subKey, object, -1L);
    }

    @Override
    public void put2CommonHashCache(String hashKey, String subKey, Object object, long timeToLive) {
        if (hashKey == null || subKey == null || object == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2HashCache [hashKey: [{}], subKey: [{}], timeToLive: [{} ms]",
                    new Object[] {hashKey, subKey, Long.valueOf(timeToLive)});
        }
        Map<String, Object> cache = null;
        if (COMMON_CACHE.asMap().containsKey(hashKey)) {
            Object currCache = COMMON_CACHE.asMap().get(hashKey);
            if ((currCache instanceof Map)) {
                cache = (Map<String, Object>) currCache;
            }
        }
        if (cache == null) {
            cache = new MapMaker().initialCapacity(1).makeMap();
            COMMON_CACHE.put(hashKey, cache);
        }
        if (timeToLive > 0L) {
            cache.put(subKey, CacheObject.valueOf(object, timeToLive));
        } else {
            cache.put(subKey, object);
        }
    }
    
    @Override
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object object) {
        return put2CommonHashCacheIfAbsent(hashKey, subKey, object, -1L);
    }

    @Override
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object object,
            long timeToLive) {
        if (hashKey == null || subKey == null || object == null) {
            return object;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2HashCache [hashKey: [{}], subKey: [{}], timeToLive: [{} ms]",
                    new Object[] {hashKey, subKey, Long.valueOf(timeToLive)});
        }
        ConcurrentMap<String, Object> cache = null;
        if (COMMON_CACHE.asMap().containsKey(hashKey)) {
            Object currCache = COMMON_CACHE.asMap().get(hashKey);
            if ((currCache instanceof Map)) {
                cache = (ConcurrentMap<String, Object>) currCache;
            }
        }
        if (cache == null) {
            cache = new MapMaker().initialCapacity(1).makeMap();
            COMMON_CACHE.put(hashKey, cache);
        }
        if (timeToLive > 0L) {
            CacheObject cacheObject =
                    (CacheObject) cache
                            .putIfAbsent(subKey, CacheObject.valueOf(object, timeToLive));
            if (cacheObject == null) {
                return null;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "createTime: [{}], ttl: [{}} , isValidate: [{}]",
                        new Object[] {Long.valueOf(cacheObject.getCreateTime()),
                                Long.valueOf(cacheObject.getTtl()), Boolean.valueOf(cacheObject.isValidate())});
            }
            if (!cacheObject.isValidate()) {
                if (cache.containsKey(subKey)) {
                    cache.remove(subKey);
                }
                return null;
            }
            return cacheObject.getEntity();
        }
        return cache.putIfAbsent(subKey, object);
    }

    @Override
    public Object getFromCommonCache(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getFromCache-Key:[{}]", key);
        }
        Object cacheObj = COMMON_CACHE.asMap().get(key);
        if (cacheObj == null) {
            return null;
        }
        if ((cacheObj instanceof CacheObject)) {
            CacheObject co = (CacheObject) cacheObj;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CacheObject- [createTime: [{}] , ttl: [{}] , isValidate: [{}] ]",
                        new Object[] {Long.valueOf(co.getCreateTime()), Long.valueOf(co.getTtl()),
                                Boolean.valueOf(co.isValidate())});
            }
            if (!co.isValidate()) {
                if (COMMON_CACHE.asMap().containsKey(key)) {
                    COMMON_CACHE.asMap().remove(key);
                }
                return null;
            }
            return co.getEntity();
        }
        return cacheObj;
    }

    @Override
    public Object getFromCommonHashCache(String hashKey, String subKey) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("hashKey: [{}] , subKey: [{}]", hashKey, subKey);
        }
        Object cacheObject = COMMON_CACHE.asMap().get(hashKey);
        if (cacheObject == null) {
            return null;
        }
        if ((cacheObject instanceof Map)) {
            Map<String, Object> cache = (Map<String, Object>) cacheObject;
            Object subObject = cache.get(subKey);
            if (subObject == null) {
                return null;
            }
            if ((subObject instanceof CacheObject)) {
                CacheObject co = (CacheObject) subObject;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                            "createTime: [{}], ttl: [{}} , isValidate: [{}]",
                            new Object[] {Long.valueOf(co.getCreateTime()),
                                    Long.valueOf(co.getTtl()), Boolean.valueOf(co.isValidate())});
                }
                if (!co.isValidate()) {
                    if (cache.containsKey(subKey)) {
                        cache.remove(subKey);
                    }
                    return null;
                }
                return co.getEntity();
            }
            return subObject;
        }
        return null;
    }

    @Override
    public void removeFromCommonCache(String key) {
        if (COMMON_CACHE.asMap().containsKey(key)) {
            COMMON_CACHE.asMap().remove(key);
        }
    }

    @Override
    public void removeFromCommonHashCache(String hashKey, String subKey) {
        Object cache = COMMON_CACHE.asMap().get(hashKey);
        if ((cache == null) || (!(cache instanceof Map))) {
            return;
        }
        Map<String, Object> map = (Map<String, Object>) cache;
        if (map.containsKey(subKey)) {
            map.remove(subKey);
        }
    }

    @Override
    public void clearValidateCacheObject(boolean clearInValidCommonCache) {
        Set<String> entiyKeySet = new HashSet<String>(ENTITY_CACHE.asMap().keySet());
        for (String key : entiyKeySet) {
            CacheObject cacheObject = (CacheObject) ENTITY_CACHE.asMap().get(key);
            if (cacheObject != null) {
                if (!cacheObject.isValidate()) {
                    synchronized (cacheObject) {
                        if (!cacheObject.isValidate()) {
                            Object entity = cacheObject.getEntity();
                            if (!dbService.isInDbQueue(entity)) {
                                ENTITY_CACHE.asMap().remove(key);
                            } else {
                                cacheObject.increaseExpireTime(ONE_MIN_MILISECONDS,
                                        MAX_EXTEND_MILISECONDS);
                            }
                        }
                    }
                }
            }
        }
        if (clearInValidCommonCache) {
            Set<String> commonKeySet = new HashSet<String>(COMMON_CACHE.asMap().keySet());
            for (String key : commonKeySet) {
                Object object = COMMON_CACHE.asMap().get(key);
                if (object != null) {
                    if ((object instanceof CacheObject)) {
                        if (!((CacheObject) object).isValidate()) {
                            synchronized (object) {
                                if (!((CacheObject) object).isValidate()) {
                                    COMMON_CACHE.asMap().remove(key);
                                }
                            }
                        }
                    } else if ((object instanceof Map)) {
                        Map<String, Object> cacheMap = (Map<String, Object>) object;
                        Set<String> ckSet = new HashSet<String>(cacheMap.keySet());
                        for (String ck : ckSet) {
                            Object object2 = cacheMap.get(ck);
                            if (object2 != null) {
                                if (((object2 instanceof CacheObject))
                                        && (!((CacheObject) object2).isValidate())) {
                                    synchronized (object2) {
                                        if (!((CacheObject) object2).isValidate()) {
                                            cacheMap.remove(ck);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
