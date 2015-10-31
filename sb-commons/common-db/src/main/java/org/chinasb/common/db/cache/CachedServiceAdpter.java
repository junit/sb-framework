package org.chinasb.common.db.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.chinasb.common.db.dao.CommonDao;
import org.chinasb.common.db.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 缓存服务适配器
 * @author zhujuan
 */
public abstract class CachedServiceAdpter {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Qualifier("commonDaoImpl")
    @Autowired(required = true)
    protected CommonDao commonDao;
    @Autowired
    protected CachedService cachedService;
    /**
     * 类对象锁缓存集合
     */
    private static final LoadingCache<String, Object> OBJECT_MAPS = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, Object>() {
                @Override
                public Object load(String clz) {
                    return new Object();
                }
            });
    
    /**
     * 获取实体键名
     * @param id
     * @param entityClazz
     * @return
     */
    protected <PK extends Comparable<PK> & Serializable> String getEntityIdKey(PK id,
            Class<?> entityClazz) {
        return entityClazz.getName() + "_" + id;
    }

    /**
     * 获取实体
     * @param id
     * @param clazz
     * @return
     */
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T get(PK id,
            Class<T> clazz) {
        return get(id, clazz, true);
    }

    /**
     * 获取实体
     * @param id
     * @param clazz
     * @param fromCache true:获取缓存中的数据， false:获取DB中的数据
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T get(PK id,
            Class<T> clazz, boolean fromCache) {
        if (id == null) {
            return null;
        }
        String key = getEntityIdKey(id, clazz);
        if (!fromCache) {
            cachedService.removeFromEntityCache(key);
        } else {
            T entity = (T) cachedService.getFromEntityCache(key);
            if (entity != null) {
                return entity;
            }
        }
        
        Object lockObject = OBJECT_MAPS.getUnchecked(key);
        try {
            synchronized (lockObject) {
                T entity = (T) cachedService.getFromEntityCache(key);
                if (entity != null) {
                    return entity;
                }
                entity = (T) getEntityFromDB(id, clazz);
                return (T) cachedService.put2EntityCache(key, entity);
            }
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
        return null;
    }


    /**
     * 获取实体集合
     * @param idList
     * @param entityClazz
     * @return
     */
    protected <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> List<T> getEntityFromIdList(
            Collection<PK> idList, Class<T> entityClazz) {
        List<T> entityList = new ArrayList<T>();
        if ((idList == null) || (idList.isEmpty())) {
            return entityList;
        }
        for (PK entityId : idList) {
            T entity = get(entityId, entityClazz);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        return entityList;
    }
    
    /**
     * 获取实体缓存
     * @param id
     * @param entityClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T getEntityFromCache(
            PK id, Class<T> entityClazz) {
        return (T) cachedService.getFromEntityCache(getEntityIdKey(id, entityClazz));
    }

    /**
     * 获取实体
     * @param id
     * @param entityClazz
     * @return
     */
    protected <T, PK extends Serializable> T getEntityFromDB(PK id, Class<T> entityClazz) {
        return commonDao.get(id, entityClazz);
    }
    
    /**
     * 移除实体缓存
     * @param id
     * @param entityClazz
     */
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> void removeEntityFromCache(
            PK id, Class<T> entityClazz) {
        cachedService.removeFromEntityCache(getEntityIdKey(id, entityClazz));
    }

    /**
     * 移除实体缓存
     * @param idList
     * @param entityClazz
     */
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> void removeEntityFromCache(
            Collection<PK> idList, Class<T> entityClazz) {
        if ((idList != null) && (!idList.isEmpty())) {
            for (PK id : idList) {
                cachedService.removeFromEntityCache(getEntityIdKey(id, entityClazz));
            }
        }
    }
    
    /**
     * 添加实体缓存
     * @param entiys
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> List<T> put2EntityCache(
            T... entiys) {
        if ((entiys != null) && (entiys.length > 0)) {
            List<T> result = new ArrayList<T>();
            for (T entiy : entiys) {
                result.add((T) cachedService.put2EntityCache(
                        getEntityIdKey(entiy.getId(), entiy.getClass()), entiy));
            }
            return result;
        }
        return new ArrayList<T>(0);
    }

    /**
     * 添加实体缓存
     * @param entiys
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> List<T> put2EntityCache(
            Collection<T> entiys) {
        if ((entiys != null) && (entiys.size() > 0)) {
            List<T> result = new ArrayList<T>();
            for (T entiy : entiys) {
                result.add((T) cachedService.put2EntityCache(
                        getEntityIdKey(entiy.getId(), entiy.getClass()), entiy));
            }
            return result;
        }
        return new ArrayList<T>(0);
    }


    /**
     * 清除过期缓存
     * @param clearInValidCommonCache 是否清除通用缓存标志{true：清除, false:忽略}
     */
    protected void clearInValidateCacheObject(boolean clearInValidCommonCache) {
        cachedService.clearInValidateCacheObject(clearInValidCommonCache);
    }
}
