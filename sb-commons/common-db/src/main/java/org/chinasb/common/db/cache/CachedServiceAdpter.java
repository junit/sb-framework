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

/**
 * 缓存管理适配器
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
     * 获得实体主健
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     * @return
     */
    protected <PK extends Comparable<PK> & Serializable> String getEntityIdKey(PK id,
            Class<?> entityClazz) {
        return entityClazz.getName() + "_" + id;
    }

    /**
     * 获得实体
     * @param id 唯一标识
     * @param clazz 实体类对象
     * @return
     */
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T get(PK id,
            Class<T> clazz) {
        return get(id, clazz, true);
    }

    /**
     * 获得实体
     * @param id 唯一标识
     * @param clazz 实体类对象
     * @param fromCache true:通过缓存获得， false:通过数据库获得
     * @return
     */
    protected <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T get(PK id,
            Class<T> clazz, boolean fromCache) {
        if (id == null) {
            return null;
        }
        String key = getEntityIdKey(id, clazz);
        if (!fromCache) {
            cachedService.removeFromEntityCache(key);
        }
        try {
            T entity = (T) cachedService.getFromEntityCache(key);
            if (entity != null) {
                return entity;
            }
            entity = (T) getEntityFromDB((Serializable) id, clazz);
            return (T) cachedService.put2EntityCache(key, entity);
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
        return null;
    }


    /**
     * 获得实体对象集合（缓存->数据库）
     * @param idList 唯一标识集合
     * @param entityClazz 实体类对象
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
     * 通过缓存获得实体
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     * @return
     */
    protected <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> T getEntityFromCache(
            PK id, Class<T> entityClazz) {
        String key = getEntityIdKey(id, entityClazz);
        return (T) cachedService.getFromEntityCache(key);
    }

    /**
     * 通过数据库获得实体
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     * @return
     */
    protected <T, PK extends Serializable> T getEntityFromDB(PK id, Class<T> entityClazz) {
        return commonDao.get(id, entityClazz);
    }
    
    /**
     * 移除实体缓存
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     */
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> void removeEntityFromCache(
            PK id, Class<T> entityClazz) {
        cachedService.removeFromEntityCache(getEntityIdKey(id, entityClazz));
    }

    /**
     * 移除实体缓存
     * @param idList 唯一标识集合
     * @param entityClazz 实体类对象
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
     * 增加实体缓存
     * @param entiys 实体
     * @return
     */
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
     * 增加实体缓存
     * @param entiys 实体集合
     * @return
     */
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
     * @param clearInValidCommonCache 清除公共缓存标志{true：清除, false:忽略}
     */
    protected void clearValidateCacheObject(boolean clearInValidCommonCache) {
        cachedService.clearValidateCacheObject(clearInValidCommonCache);
    }
}
