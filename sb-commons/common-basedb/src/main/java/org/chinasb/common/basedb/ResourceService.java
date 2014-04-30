package org.chinasb.common.basedb;

import java.util.Collection;
import java.util.List;

/**
 * 基础数据管理
 * @author zhujuan
 */
public interface ResourceService {
    /**
     * 通过唯一标识获得基础数据
     * @param id 唯一标识
     * @param clazz 基础数据的对象类
     * @return
     */
    public <T> T get(Object id, Class<T> clazz);

    /**
     * 通过索引获得基础数据集合
     * @param indexName 索引名称
     * @param clazz 基础数据的对象类
     * @param indexValues 索引值
     * @return
     */
    public <T> List<T> listByIndex(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 通过索引获得基础数据唯一标识集合
     * @param indexName 索引名称
     * @param clazz 基础数据的对象类
     * @param pk 唯一标识的数据类型
     * @param indexValues 索引值
     * @return
     */
    public <T, PK> List<PK> listIdByIndex(String indexName, Class<T> clazz, Class<PK> pk,
            Object... indexValues);

    /**
     * 通过索引获得基础数据的第一条记录
     * @param indexName 索引名称
     * @param clazz 基础数据的对象类
     * @param indexValues 索引值
     * @return
     */
    public <T> T getByUnique(String indexName, Class<T> clazz, Object... indexValues);

    /**
     * 获得全部基础数据集合
     * @param clazz 基础数据的对象类
     * @return
     */
    public <T> Collection<T> listAll(Class<T> clazz);

    /**
     * 增加基础数据到索引数据集合
     * @param indexName 索引名称
     * @param id 唯一标识
     * @param clazz 基础数据的对象类
     */
    public <T> void addToIndex(String indexName, Object id, Class<T> clazz);

    /**
     * 增加基础数据到索引数据集合
     * @param indexName 索引名称
     * @param id 唯一标识
     * @param clazz 基础数据的对象类
     * @param indexValues 索引值
     */
    public <T> void addToIndex(String indexName, Object id, Class<T> clazz, Object... indexValues);

    /**
     * 重新加载全部基础数据
     */
    public void reloadAll();
}
