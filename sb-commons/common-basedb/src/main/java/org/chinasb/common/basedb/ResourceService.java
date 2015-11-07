package org.chinasb.common.basedb;

import java.util.List;

/**
 * 基础数据管理服务接口
 * @author zhujuan
 */
interface ResourceService {
    /**
     * 获取基础数据
     * @param id ID
     * @param clazz 基础数据类对象
     * @return
     */
    <T> T get(Object id, Class<T> clazz);

    /**
     * 通过索引获取基础数据列表
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param indexValues 索引值
     * @return
     */
    <T> List<T> listByIndex(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 通过索引获取基础数据ID列表
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param pk 基础数据ID类对象
     * @param indexValues 索引值
     * @return
     */
    <T, PK> List<PK> listIdByIndex(String indexName, Class<T> clazz, Class<PK> pk,
            Object... indexValues);

    /**
     * 通过索引获取基础数据的唯一记录
     * @param indexName 索引名称
     * @param clazz 基础数据类对象
     * @param indexValues 索引值
     * @return
     */
    <T> T getByUnique(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 获取全部基础数据列表
     * @param clazz 基础数据类对象
     * @return
     */
    <T> List<T> listAll(Class<T> clazz);

    /**
     * 添加基础数据ID索引
     * @param indexName 索引名称
     * @param id 基础数据ID
     * @param clazz 基础数据类对象
     */
    <T> void addToIndex(String indexName, Object id, Class<T> clazz);

    /**
     * 添加基础数据ID索引
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
