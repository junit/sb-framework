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
     * @param paramObject 唯一标识
     * @param paramClass 基础数据的对象类
     * @return
     */
    public <T> T get(Object paramObject, Class<T> paramClass);

    /**
     * 通过索引获得基础数据集合
     * @param paramString 索引名称
     * @param paramClass 基础数据的对象类
     * @param paramVarArgs 索引值
     * @return
     */
    public <T> List<T> listByIndex(String paramString, Class<T> paramClass, Object... paramVarArgs);

    /**
     * 通过索引获得基础数据唯一标识集合
     * @param paramString 索引名称
     * @param paramClass 基础数据的对象类
     * @param paramClass1 唯一标识的数据类型
     * @param paramVarArgs 索引值
     * @return
     */
    public <T, PK> List<PK> listIdByIndex(String paramString, Class<T> paramClass,
            Class<PK> paramClass1, Object... paramVarArgs);

    /**
     * 通过索引获得基础数据的第一条记录
     * @param paramString 索引名称
     * @param paramClass 基础数据的对象类
     * @param paramVarArgs 索引值
     * @return
     */
    public <T> T getByUnique(String paramString, Class<T> paramClass, Object... paramVarArgs);

    /**
     * 获得全部基础数据集合
     * @param paramClass 基础数据的对象类
     * @return
     */
    public <T> Collection<T> listAll(Class<T> paramClass);

    /**
     * 增加基础数据到索引数据集合
     * @param paramString 索引名称
     * @param paramObject 唯一标识
     * @param paramClass 基础数据的对象类
     */
    public <T> void addToIndex(String paramString, Object paramObject, Class<T> paramClass);

    /**
     * 增加基础数据到索引数据集合
     * @param paramString 索引名称
     * @param paramObject 唯一标识
     * @param paramClass 基础数据的对象类
     * @param paramVarArgs 索引值
     */
    public <T> void addToIndex(String paramString, Object paramObject, Class<T> paramClass,
            Object... paramVarArgs);

    /**
     * 重新加载全部基础数据
     */
    public void reloadAll();
}
