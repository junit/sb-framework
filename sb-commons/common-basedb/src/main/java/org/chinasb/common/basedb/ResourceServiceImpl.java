package org.chinasb.common.basedb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.chinasb.common.basedb.annotation.Resource;
import org.chinasb.common.utility.PackageUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 基础数据管理
 * @author zhujuan
 */
@Component
public class ResourceServiceImpl
        implements
            ResourceService,
            ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private ConcurrentHashMap<Class, Storage> storages = new ConcurrentHashMap<Class, Storage>(50);
    private List<ResourceListener> listeners = new ArrayList<ResourceListener>();
    @Autowired(required = false)
    @Qualifier("basedb_location")
    private String resourceLocation = "res_db" + File.separator;
    @Qualifier("spring_refresh_event_reload")
    private Boolean springRefreshEventReload = Boolean.valueOf(false);
    @Autowired(required = false)
    @Qualifier("basedb_package")
    private String resourcePackage = "org.chinasb.**.basedb.model";
    @Autowired
    private ApplicationContext applicationContext;
    private static final AtomicBoolean REFRESH_EVENT_SWITCH = new AtomicBoolean(false);

    /**
     * 基础数据加载
     */
    private void initialize() {
        registerListener();
        Collection<Class<?>> clazzCollection =
                PackageUtility.scanPackages(new String[] {resourcePackage});
        if ((clazzCollection != null) && (!clazzCollection.isEmpty())) {
            for (Class<?> clazz : clazzCollection) {
                try {
                    if (clazz.isAnnotationPresent(Resource.class)) {
                        initializeStorage(clazz);
                    }
                } catch (Exception e) {
                    FormattingTuple message =
                            MessageFormatter.format("加载  {} 基础数据时出错!", clazz.getName());
                    log.error(message.getMessage(), e);
                }
            }
        } else {
            FormattingTuple message = MessageFormatter.format("在 {} 包下没有扫描到实体类!", resourcePackage);
            log.error(message.getMessage());
        }
        fireBasedbReload();
        log.info("基础数据加载完毕...");
    }

    /**
     * 基础数据监听器重载
     */
    private void fireBasedbReload() {
        if ((listeners != null) && (!listeners.isEmpty())) {
            for (ResourceListener listener : listeners) {
                try {
                    listener.onBasedbReload();
                } catch (Exception e) {
                    log.error("BasedbListener 出错!", e);
                }
            }
        }
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        Map<String, ResourceListener> listenerMap =
                applicationContext.getBeansOfType(ResourceListener.class);
        if (listenerMap != null) {
            for (ResourceListener listener : listenerMap.values()) {
                listeners.add(listener);
            }
        }
    }

    /**
     * 初始化基础数据
     * @param clazz
     */
    private void initializeStorage(Class clazz) {
        Storage storage = storages.get(clazz);
        if (storage == null) {
            storage = new Storage(clazz, resourceLocation, applicationContext);
            storages.putIfAbsent(clazz, storage);
            storage = storages.get(clazz);
        }
        storage.reload();
    }

    /**
     * 获得基础数据仓库
     * @param clazz
     * @return
     */
    private Storage getStorage(Class clazz) {
        return storages.get(clazz);
    }

    @Override
    public <T> T get(Object id, Class<T> clazz) {
        Storage<?> storage = getStorage(clazz);
        if (storage != null) {
            return (T) storage.get(id);
        }
        return null;
    }

    @Override
    public <T> List<T> listByIndex(String indexName, Class<T> clazz, Object... indexValues) {
        Storage<?> storage = getStorage(clazz);
        if (storage != null) {
            return (List<T>) storage.getIndex(indexName, indexValues);
        }
        return new ArrayList<T>(0);
    }

    @Override
    public <T, PK> List<PK> listIdByIndex(String indexName, Class<T> clazz, Class<PK> pk,
            Object... indexValues) {
        Storage storage = getStorage(clazz);
        if (storage != null) {
            return storage.getIndexIdList(indexName, indexValues);
        }
        return new ArrayList<PK>(0);
    }

    @Override
    public <T> T getByUnique(String indexName, Class<T> clazz, Object... indexValues) {
        Storage storage = getStorage(clazz);
        if (storage != null) {
            List list = storage.getIndex(indexName, indexValues);
            return (T) ((list != null) && (!list.isEmpty()) ? list.get(0) : null);
        }
        return null;
    }

    @Override
    public <T> Collection<T> listAll(Class<T> clazz) {
        Storage<?> storage = getStorage(clazz);
        if (storage != null) {
            return (Collection<T>) storage.listAll();
        }
        return new ArrayList<T>(0);
    }

    @Override
    public <T> void addToIndex(String indexName, Object id, Class<T> clazz) {
        Storage storage = getStorage(clazz);
        if (storage == null) {
            return;
        }
        Map indexTable = storage.getIndexTable();
        if (indexTable == null) {
            return;
        }
        String newIndexKey = KeyBuilder.buildIndexKey(clazz, indexName, new Object[0]);
        List idList = (List) indexTable.get(newIndexKey);
        if (idList == null) {
            idList = new ArrayList();
            indexTable.put(newIndexKey, idList);
        }
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    @Override
    public <T> void addToIndex(String indexName, Object id, Class<T> clazz, Object... indexValues) {
        Storage storage = getStorage(clazz);
        if (storage == null) {
            return;
        }
        String newIndexKey = KeyBuilder.buildIndexKey(clazz, indexName, indexValues);
        Map indexTable = storage.getIndexTable();
        if (indexTable == null) {
            return;
        }
        List idList = (List) indexTable.get(newIndexKey);
        if (idList == null) {
            idList = new ArrayList();
            indexTable.put(newIndexKey, idList);
        }
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    @Override
    public void reloadAll() {
        for (Class clazz : storages.keySet()) {
            try {
                initializeStorage(clazz);
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("加载 {} 类时出错!", clazz.getName());
                log.error(message.getMessage(), e);
            }
        }
        fireBasedbReload();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if ((!springRefreshEventReload.booleanValue()) && (REFRESH_EVENT_SWITCH.get())) {
            return;
        }
        REFRESH_EVENT_SWITCH.set(true);
        try {
            initialize();
        } catch (Exception e) {
            log.error("基础数据初始化出错!", e);
        }
        applicationContext.publishEvent(new ResourceReloadEvent(applicationContext));
    }
}
