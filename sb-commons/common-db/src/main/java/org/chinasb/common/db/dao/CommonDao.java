package org.chinasb.common.db.dao;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;

/**
 * 数据库通用组件
 * @author zhujuan
 */
public interface CommonDao {
    /**
     * 获得实体对象
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     * @return
     */
    public <T> T get(Serializable id, Class<T> entityClazz);
    
    /**
     * 保存实体对象
     * @param entities 实体对象
     */
    public <T> void save(T... entities);

    /**
     * 更新实体对象
     * @param entities 实体对象
     */
    public <T> void update(T... entities);

    /**
     * 更新实体对象集合
     * @param entities 实体对象集合
     */
    public <T> void update(Collection<T> entities);

    /**
     * 删除实体对象
     * @param id 唯一标识
     * @param entityClazz 实体类对象
     */
    public <T> void delete(Serializable id, Class<T> entityClazz);

    /**
     * 离线查询
     * @param detachedCriteria
     * @return
     */
    public <T> T execute(DetachedCriteria detachedCriteria);

    /**
     * Native SQL
     * @param sql
     * @return
     */
    public <T> T execute(String sql);
}
