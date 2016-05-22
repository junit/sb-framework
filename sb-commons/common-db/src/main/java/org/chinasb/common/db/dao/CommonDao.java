package org.chinasb.common.db.dao;

import java.io.Serializable;
import java.util.Collection;

import org.chinasb.common.db.model.BaseModel;
import org.hibernate.criterion.DetachedCriteria;

/**
 * 通用DAO
 * 
 * @author zhujuan
 */
public interface CommonDao {
    /**
     * 获取实体
     * 
     * @param id
     * @param entityClazz
     * @return {@link T}
     */
    <T> T get(Serializable id, Class<T> entityClazz);

    /**
     * 保存实体
     * 
     * @param entities
     */
    @SuppressWarnings("unchecked")
    <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> void save(T... entities);

    /**
     * 更新实体
     * 
     * @param entities
     */
    @SuppressWarnings("unchecked")
    <T> void update(T... entities);

    /**
     * 更新实体集合
     * 
     * @param entities
     */
    <T> void update(Collection<T> entities);

    /**
     * 删除实体
     * 
     * @param id
     * @param entityClazz
     */
    <T> void delete(Serializable id, Class<T> entityClazz);

    /**
     * 离线查询
     * 
     * @param detachedCriteria
     * @return {@link T}
     */
    <T> T execute(DetachedCriteria detachedCriteria);

    /**
     * 原生SQL执行
     * 
     * @param sql
     * @return {@link T}
     */
    int execute(String sql);

    /**
     * 原生SQL查询
     * 
     * @param sql
     * @return {@link T}
     */
    <T> T query(String sql);

    /**
     * 原生SQL查询
     * 
     * @param sql
     * @param entityClazz
     * @return {@link T}
     */
    <T> T query(String sql, Class<T> entityClazz);
}
