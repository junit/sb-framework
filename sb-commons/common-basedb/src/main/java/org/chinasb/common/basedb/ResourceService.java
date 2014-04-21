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
     * @param paramObject
     * @param paramClass
     * @return
     */
    public <T> T get(Object paramObject, Class<T> paramClass);

    /**
     * 通过索引获得基础数据集合
     * @param paramString
     * @param paramClass
     * @param paramVarArgs
     * @return
     */
    public <T> List<T> listByIndex(String paramString, Class<T> paramClass, Object... paramVarArgs);

    /**
     * 通过索引获得基础数据唯一标识集合
     * @param paramString
     * @param paramClass
     * @param paramClass1
     * @param paramVarArgs
     * @return
     */
    public <T, PK> List<PK> listIdByIndex(String paramString, Class<T> paramClass,
            Class<PK> paramClass1, Object... paramVarArgs);

    /**
     * 通过索引获得基础数据的第一条记录
     * @param paramString
     * @param paramClass
     * @param paramVarArgs
     * @return
     */
    public <T> T getByUnique(String paramString, Class<T> paramClass, Object... paramVarArgs);

    /**
     * 获得基础数据集合
     * @param paramClass
     * @return
     */
    public <T> Collection<T> listAll(Class<T> paramClass);

    /**
     * 增加基础数据到索引
     * @param paramString
     * @param paramObject
     * @param paramClass
     */
    public <T> void addToIndex(String paramString, Object paramObject, Class<T> paramClass);

    /**
     * 增加基础数据到索引
     * @param paramString
     * @param paramObject
     * @param paramClass
     * @param paramVarArgs
     */
    public <T> void addToIndex(String paramString, Object paramObject, Class<T> paramClass,
            Object... paramVarArgs);

    /**
     * 重新加载所有基础数据
     */
    public void reloadAll();
}
