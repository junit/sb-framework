package org.chinasb.common.basedb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 资源适配器
 * @author zhujuan
 */
public abstract class ResourceAdapter implements ResourceListener {
    
    @Autowired
    protected ResourceService resourceService;

    @Override
    public void onBasedbReload() {
        initialize();
    }

    /**
     * 获取基础数据列表
     * @param clazz 基础数据类对象
     * @param idList 基础数据ID列表
     * @return
     */
    protected <T> List<T> getFromIdList(Class<T> clazz, Collection<?> idList) {
        if ((idList != null) && (!idList.isEmpty())) {
            List<T> entityList = new ArrayList<T>();
            for (Object id : idList) {
                T entity = resourceService.get(id, clazz);
                if (entity != null) {
                    entityList.add(entity);
                }
            }
            return Collections.unmodifiableList(entityList);
        }
        return null;
    }

    /**
     * 初始化
     */
    public abstract void initialize();
}
