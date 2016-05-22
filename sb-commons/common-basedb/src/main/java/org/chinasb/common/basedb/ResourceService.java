package org.chinasb.common.basedb;

import java.util.List;

/**
 * 基础数据服务接口
 * 
 * @author zhujuan
 */
public interface ResourceService {
    /**
     * 通过id获取基础数据
     * 
     * @param id 主健
     * @param clazz 基础数据类对象
     * @return T
     */
    <T> T get(Object id, Class<T> clazz);

    /**
     * 通过索引键、值获取基础数据列表
     * 
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param indexValues 索引值(顺序对应 {@link Index#order})
     * @return List<T>
     */
    <T> List<T> listByIndex(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 通过索引获取基础数据ID列表
     * 
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param pk 基础数据主键类对象
     * @param indexValues 索引值
     * @return {@link List}			返回对应的索引ID列表
     */
    <T, PK> List<PK> listIdByIndex(String indexName, Class<T> clazz, Class<PK> pk,
            Object... indexValues);

    /**
     * 通过索引键、值获取基础数据的唯一记录
     * 
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param indexValues 索引值(顺序对应 {@link Index#order})
     * @return T
     */
    <T> T getByUnique(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 获取全部基础数据列表
     * 
     * @param clazz 基础数据类对象
     * @return List<T>
     */
    <T> List<T> listAll(Class<T> clazz);

    /**
     * 添加基础数据ID索引
     * 
     * @param indexName 索引名称
     * @param id 基础数据ID
     * @param clazz 基础数据类对象
     */
    <T> void addToIndex(String indexName, Object id, Class<T> clazz);

    /**
     * 添加基础数据ID索引
     * 
     * @param indexName 索引名称
     * @param id 基础数据ID
     * @param clazz 基础数据类对象
     * @param indexValues 索引值
     */
    <T> void addToIndex(String indexName, Object id, Class<T> clazz, Object... indexValues);

    /**
     * 重新加载全部基础数据
     */
    void reloadAll();
}
