package org.chinasb.common.db.cache.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.chinasb.common.db.cache.CachedService;
import org.chinasb.common.db.executor.DbService;
import org.chinasb.common.db.model.CacheObject;
import org.chinasb.common.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;

/**
 * 缓存服务
 * 
 * @author zhujuan
 */
@Service
public class CacheServiceImpl implements CachedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);
    private static final int ONE_MIN_MILISECONDS = Constants.ONE_MINUTE_MILLISECOND;
    private static final int MAX_EXTEND_MILISECONDS = Constants.ONE_MINUTE_MILLISECOND * 10;

    /**
     * 通用缓存容量大小
     */
    @Autowired(required = false)
    @Qualifier("dbcache.max_capacity_of_common_cache")
    private Integer commonCacheSize = 500000;
    /**
     * 实体缓存容量大小
     */
    @Autowired(required = false)
    @Qualifier("dbcache.max_capacity_of_entity_cache")
    private Integer entityCacheSize = 500000;
    /**
     * 实体缓存存活时间
     */
    @Autowired(required = false)
    @Qualifier("dbcache.ttl_of_entity_cache")
    private Integer entityCacheTTL = ONE_MIN_MILISECONDS * 120;

    @Autowired
    private DbService dbService;

    /**
     * 通用缓存
     */
    private ConcurrentMap<String, Object> COMMON_CACHE = null;
    /**
     * 实体缓存
     */
    private ConcurrentMap<String, CacheObject> ENTITY_CACHE = null;

    /**
     * 缓存初始化
     */
    @PostConstruct
    protected void initialize() {
        Cache<String, Object> COMMON =
                CacheBuilder.newBuilder().maximumSize(commonCacheSize.intValue()).build();
        COMMON_CACHE = COMMON.asMap();
        Cache<String, CacheObject> ENTITY =
                CacheBuilder.newBuilder().maximumSize(entityCacheSize.intValue()).build();
        ENTITY_CACHE = ENTITY.asMap();
    }

    @Override
    public Object put2EntityCache(String key, Object value) {
        return put2EntityCache(key, value, -1L);
    }

    @Override
    public Object put2EntityCache(String key, Object value, long timeToLive) {
        if ((key == null) || (value == null)) {
            return value;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2EntityCache [key: [{}], timeToLive: [{} ms]",
                    new Object[] {key, Long.valueOf(timeToLive)});
        }
        if (timeToLive > 0L) {
            ENTITY_CACHE.putIfAbsent(key, CacheObject.valueOf(value, timeToLive));
        } else {
            ENTITY_CACHE.putIfAbsent(key, CacheObject.valueOf(value, entityCacheTTL.intValue()));
        }
        return getFromEntityCache(key);
    }

    @Override
    public Object getFromEntityCache(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getFromEntityCache-Key:[{}]", key);
        }
        CacheObject cacheObject = ENTITY_CACHE.get(key);
        if (cacheObject == null) {
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "CacheObject- [key: [{}], createTime: [{}] , ttl: [{}] , isValidate: [{}] ]",
                    new Object[] {key, Long.valueOf(cacheObject.getCreateTime()),
                            Long.valueOf(cacheObject.getTtl()),
                            Boolean.valueOf(cacheObject.isValidate())});
        }
        if (!cacheObject.isValidate()) {
            synchronized (cacheObject) {
                cacheObject = ENTITY_CACHE.get(key);
                if (cacheObject == null) {
                    return null;
                }
                if (!cacheObject.isValidate()) {
                    Object entity = cacheObject.getValue();
                    if (!dbService.isInDbQueue(entity)) {
                        ENTITY_CACHE.remove(key);
                        return null;
                    }
                    cacheObject.increaseExpireTime(MAX_EXTEND_MILISECONDS);
                }
                return cacheObject.getValue();
            }
        }
        cacheObject.increaseExpireTime(MAX_EXTEND_MILISECONDS);
        return cacheObject.getValue();
    }

    @Override
    public void removeFromEntityCache(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removeFromEntityCache [key: [{}]]", new Object[] {key});
        }
        if (ENTITY_CACHE.containsKey(key)) {
            ENTITY_CACHE.remove(key);
        }
    }

    @Override
    public void put2CommonCache(String key, Object value) {
        put2CommonCache(key, value, -1L);
    }

    @Override
    public void put2CommonCache(String key, Object value, long timeToLive) {
        if (key == null || value == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2CommonCache [key: [{}], timeToLive: [{} ms]",
                    new Object[] {key, Long.valueOf(timeToLive)});
        }
        if (timeToLive > 0L) {
            COMMON_CACHE.put(key, CacheObject.valueOf(value, timeToLive));
        } else {
            COMMON_CACHE.put(key, value);
        }
    }

    @Override
    public Object put2CommonCacheIfAbsent(String key, Object value) {
        return put2CommonCacheIfAbsent(key, value, -1L);
    }

    @Override
    public Object put2CommonCacheIfAbsent(String key, Object value, long timeToLive) {
        if (key == null || value == null) {
            return value;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2CommonCacheIfAbsent [key：[{}], timeToLive: [{} ms]]", new Object[] {
                    key, Long.valueOf(timeToLive),});
        }
        if (timeToLive > 0L) {
            CacheObject cacheObject =
                    (CacheObject) COMMON_CACHE.putIfAbsent(key,
                            CacheObject.valueOf(value, timeToLive));
            if (!cacheObject.isValidate()) {
                if (COMMON_CACHE.containsKey(key)) {
                    COMMON_CACHE.remove(key);
                }
                return null;
            }
            return cacheObject.getValue();
        }
        return COMMON_CACHE.putIfAbsent(key, value);
    }

    @Override
    public void put2CommonHashCache(String hashKey, String subKey, Object value) {
        put2CommonHashCache(hashKey, subKey, value, -1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put2CommonHashCache(String hashKey, String subKey, Object value, long timeToLive) {
        if (hashKey == null || subKey == null || value == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("put2CommonHashCache [hashKey: [{}], subKey: [{}], timeToLive: [{} ms]",
                    new Object[] {hashKey, subKey, Long.valueOf(timeToLive)});
        }
        Map<String, Object> cacheMap = null;
        Object cacheObject = COMMON_CACHE.get(hashKey);
        if (cacheObject != null) {
            if (cacheObject instanceof Map) {
                cacheMap = (Map<String, Object>) cacheObject;
            } else {
                COMMON_CACHE.remove(hashKey);
            }
        }
        if (cacheMap == null) {
            COMMON_CACHE.putIfAbsent(hashKey, new MapMaker().initialCapacity(1).makeMap());
            cacheMap = (Map<String, Object>) COMMON_CACHE.get(hashKey);
        }
        if (timeToLive > 0L) {
            cacheMap.put(subKey, CacheObject.valueOf(value, timeToLive));
        } else {
            cacheMap.put(subKey, value);
        }
    }

    @Override
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object value) {
        return put2CommonHashCacheIfAbsent(hashKey, subKey, value, -1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object put2CommonHashCacheIfAbsent(String hashKey, String subKey, Object value,
            long timeToLive) {
        if (hashKey == null || subKey == null || value == null) {
            return value;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "put2CommonHashCacheIfAbsent [hashKey: [{}], subKey: [{}], timeToLive: [{} ms]",
                    new Object[] {hashKey, subKey, Long.valueOf(timeToLive)});
        }
        Map<String, Object> cacheMap = null;
        Object cacheObject = COMMON_CACHE.get(hashKey);
        if (cacheObject != null) {
            if (cacheObject instanceof Map) {
                cacheMap = (Map<String, Object>) cacheObject;
            } else {
                COMMON_CACHE.remove(hashKey);
            }
        }
        if (cacheMap == null) {
            COMMON_CACHE.putIfAbsent(hashKey, new MapMaker().initialCapacity(1).makeMap());
            cacheMap = (Map<String, Object>) COMMON_CACHE.get(hashKey);
        }
        if (timeToLive > 0L) {
            CacheObject subCacheObject =
                    (CacheObject) cacheMap.putIfAbsent(subKey,
                            CacheObject.valueOf(value, timeToLive));
            if (!subCacheObject.isValidate()) {
                if (cacheMap.containsKey(subKey)) {
                    cacheMap.remove(subKey);
                }
                return null;
            }
            return subCacheObject.getValue();
        }
        return cacheMap.putIfAbsent(subKey, value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getFromCommonCache-Key:[{}]", key);
        }
        Object object = COMMON_CACHE.get(key);
        if (object == null) {
            return null;
        }
        if ((object instanceof CacheObject)) {
            CacheObject cacheObject = (CacheObject) object;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "CacheObject- [key: [{}], createTime: [{}] , ttl: [{}] , isValidate: [{}] ]",
                        new Object[] {key, Long.valueOf(cacheObject.getCreateTime()),
                                Long.valueOf(cacheObject.getTtl()),
                                Boolean.valueOf(cacheObject.isValidate())});
            }
            if (!cacheObject.isValidate()) {
                if (COMMON_CACHE.containsKey(key)) {
                    COMMON_CACHE.remove(key);
                }
                return null;
            }
            return cacheObject.getValue();
        }
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getFromCommonHashCache(String hashKey, String subKey) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getFromCommonHashCache-Key:[{}], subKey: [{}]", hashKey, subKey);
        }
        Object cacheObject = COMMON_CACHE.get(hashKey);
        if (cacheObject == null) {
            return null;
        }
        if (!(cacheObject instanceof Map)) {
            return null;
        }
        Map<String, Object> cacheMap = (Map<String, Object>) cacheObject;
        Object subObject = cacheMap.get(subKey);
        if (subObject == null) {
            return null;
        }
        if ((subObject instanceof CacheObject)) {
            CacheObject subCacheObject = (CacheObject) subObject;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                        "CacheObject- [hashKey: [{}], subKey: [{}], createTime: [{}] , ttl: [{}] , isValidate: [{}] ]",
                        new Object[] {hashKey, subKey,
                                Long.valueOf(subCacheObject.getCreateTime()),
                                Long.valueOf(subCacheObject.getTtl()),
                                Boolean.valueOf(subCacheObject.isValidate())});
            }
            if (!subCacheObject.isValidate()) {
                if (cacheMap.containsKey(subKey)) {
                    cacheMap.remove(subKey);
                }
                return null;
            }
            return subCacheObject.getValue();
        }
        return subObject;
    }

    @Override
    public void removeFromCommonCache(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removeFromCommonCache [key: [{}]]", new Object[] {key});
        }
        if (COMMON_CACHE.containsKey(key)) {
            COMMON_CACHE.remove(key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeFromCommonHashCache(String hashKey, String subKey) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removeFromCommonHashCache [hashKey: [{}], subKey: [{}]]", new Object[] {
                    hashKey, subKey});
        }
        Object cache = COMMON_CACHE.get(hashKey);
        if ((cache == null) || (!(cache instanceof Map))) {
            return;
        }
        Map<String, Object> map = (Map<String, Object>) cache;
        if (map.containsKey(subKey)) {
            map.remove(subKey);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clearInValidateCacheObject(boolean clearInValidCommonCache) {
        Set<String> entiyKeySet = new HashSet<String>(ENTITY_CACHE.keySet());
        for (String key : entiyKeySet) {
            CacheObject cacheObject = (CacheObject) ENTITY_CACHE.get(key);
            if (cacheObject != null) {
                if (!cacheObject.isValidate()) {
                    synchronized (cacheObject) {
                        if (!cacheObject.isValidate()) {
                            Object entity = cacheObject.getValue();
                            if (!dbService.isInDbQueue(entity)) {
                                ENTITY_CACHE.remove(key);
                            } else {
                                cacheObject.increaseExpireTime(MAX_EXTEND_MILISECONDS);
                            }
                        }
                    }
                }
            }
        }
        if (clearInValidCommonCache) {
            Set<String> commonKeySet = new HashSet<String>(COMMON_CACHE.keySet());
            for (String key : commonKeySet) {
                Object object = COMMON_CACHE.get(key);
                if (object != null) {
                    if ((object instanceof CacheObject)) {
                        if (!((CacheObject) object).isValidate()) {
                            synchronized (object) {
                                if (!((CacheObject) object).isValidate()) {
                                    COMMON_CACHE.remove(key);
                                }
                            }
                        }
                    } else if ((object instanceof Map)) {
                        Map<String, Object> cacheMap = (Map<String, Object>) object;
                        Set<String> ckSet = new HashSet<String>(cacheMap.keySet());
                        for (String ck : ckSet) {
                            Object subObject = cacheMap.get(ck);
                            if (subObject != null) {
                                if (((subObject instanceof CacheObject))
                                        && (!((CacheObject) subObject).isValidate())) {
                                    synchronized (subObject) {
                                        if (!((CacheObject) subObject).isValidate()) {
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
