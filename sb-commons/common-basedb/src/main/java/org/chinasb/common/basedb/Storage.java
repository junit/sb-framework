package org.chinasb.common.basedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.chinasb.common.basedb.ResourceServiceImpl.KeyBuilder;
import org.chinasb.common.basedb.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/**
 * 基础数据存储对象
 * 
 * @author zhujuan
 * @param <V>
 */
public class Storage<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);

    private final ApplicationContext applicationContext;
    /**
     * 基础数据类对象
     */
    private final Class<V> clazz;
    /**
     * 资源路径
     */
    private final String resourceLocation;
    /**
     * 资源位置
     */
    private final String location;
    /**
     * 资源读取器
     */
    private final ResourceReader reader;
    /**
     * ID取值器
     */
    private final Getter identifier;
    /**
     * 索引访问者映射集合Map<索引名称,索引访问者>
     */
    private final Map<String, IndexBuilder.IndexVisitor> indexVisitors;
    /**
     * 基础数据映射集合Map<ID, 基础数据>
     */
    private final Map<Object, V> dataTable = new HashMap<Object, V>();
    /**
     * 索引映射集合Map<索引字段值域的组合名称, List<ID>>
     */
    private final Map<String, List<Object>> indexTable = new HashMap<String, List<Object>>();
    /**
     * 已排序的基础数据ID列表
     */
    private List<Object> idList = new ArrayList<Object>();

    /**
     * 构建基础数据存储对象
     * 
     * @param clazz
     * @param resourceLocation
     * @param applicationContext
     */
    public Storage(Class<V> clazz, String resourceLocation, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.resourceLocation = resourceLocation;
        this.clazz = clazz;
        Resource resource = (Resource) clazz.getAnnotation(Resource.class);
        this.location = this.resourceLocation + clazz.getSimpleName() + "." + resource.suffix();
        ResourceReader reader =
                (ResourceReader) this.applicationContext.getBean(
                        resource.type() + "ResourceReader", ResourceReader.class);
        this.reader = reader;
        this.identifier = GetterBuilder.createIdGetter(clazz);
        this.indexVisitors = IndexBuilder.createIndexVisitors(clazz);
    }

    /**
     * 获取基础数据映射集合
     * 
     * @return
     */
    public Map<Object, V> getDataTable() {
        return dataTable;
    }

    /**
     * 获取索引映射集合
     * 
     * @return
     */
    public Map<String, List<Object>> getIndexTable() {
        return indexTable;
    }

    /**
     * 获取基础数据ID列表
     * 
     * @return
     */
    public List<Object> getIdList() {
        return idList;
    }

    /**
     * 通过索引获取基础数据列表
     * 
     * @param indexName 索引名称
     * @param indexValues 索引值
     * @return
     */
    public List<V> getByIndex(String indexName, Object... indexValues) {
        List<Object> idList = getIndexIdList(indexName, indexValues);
        return list(idList);
    }

    /**
     * 获取索引ID列表
     * 
     * @param indexName 索引名称
     * @param indexValues 索引值
     * @return
     */
    public List<Object> getIndexIdList(String indexName, Object... indexValues) {
        String indexkey = getIndexKey(indexName, indexValues);
        return indexTable.get(indexkey);
    }

    /**
     * 获取基础数据
     * 
     * @param key ID
     * @return
     */
    public V get(Object key) {
        return dataTable.get(key);
    }

    /**
     * 获取基础数据列表
     * 
     * @param idList ID列表
     * @return
     */
    public List<V> list(List<Object> idList) {
        List<V> resultList = new ArrayList<V>();
        if (idList != null && !idList.isEmpty()) {
            for (Object id : idList) {
                V entity = get(id);
                if (entity != null) {
                    resultList.add(entity);
                }
            }
        }
        return resultList;
    }

    /**
     * 获取全部基础数据列表
     * 
     * @return
     */
    public List<V> listAll() {
        return new ArrayList<V>(dataTable.values());
    }

    /**
     * 基础数据仓库重新加载
     */
    public synchronized void reload() {
        try {
            URL resource = ClassUtils.getDefaultClassLoader().getResource(location);
            File file = resource != null ? ResourceUtils.getFile(resource) : null;
            if (file == null || !file.exists()) {
                resource = ResourceUtils.getURL(location);
            }
            if (resource == null) {
                FormattingTuple message =
                        MessageFormatter.format("基础数据[{}]所对应的资源文件[{}]不存在!", clazz.getName(),
                                location);
                LOGGER.error(message.getMessage());
                return;
            }
            InputStream input = resource.openStream();
            Iterator<V> it = reader.read(input, clazz);

            List<Object> idList_copy = new ArrayList<Object>();
            Map<Object, V> dataTable_copy = new HashMap<Object, V>();
            Map<String, List<Object>> indexTable_copy = new HashMap<String, List<Object>>();
            while (it.hasNext()) {
                V obj = it.next();
                if (obj instanceof InitializeBean) {
                    try {
                        ((InitializeBean) obj).afterPropertiesSet();
                    } catch (Exception e) {
                        FormattingTuple message =
                                MessageFormatter.format("基础数据[{}] 属性设置后处理出错!", clazz.getName());
                        LOGGER.error(message.getMessage(), e);
                    }
                }
                if (offer(obj, dataTable_copy) != null) {
                    throw new RuntimeException(String.format("重复异常: [%s]", new Object[] {obj}));
                }
                index(obj, indexTable_copy);
                idList_copy.add(identifier.getValue(obj));
            }
            sort(idList_copy, indexTable_copy, dataTable_copy);

            idList.clear();
            indexTable.clear();
            dataTable.clear();

            dataTable.putAll(dataTable_copy);
            indexTable.putAll(indexTable_copy);
            idList.addAll(idList_copy);
            LOGGER.info("完成加载  {} 基础数据...", clazz.getName());
        } catch (IOException e) {
            FormattingTuple message =
                    MessageFormatter.format("基础数据[{}]所对应的资源文件[{}]不存在!", clazz.getName(), location);
            LOGGER.error(message.getMessage());
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    /**
     * 获取索引键值(类名&索引名称#索引值1^索引值2)
     * 
     * @param name 索引名称
     * @param value 索引值
     * @return
     */
    private String getIndexKey(String name, Object... value) {
        return KeyBuilder.buildIndexKey(clazz, name, value);
    }

    /**
     * 添加基础数据
     * 
     * @param value 基础数据
     * @param dataTable 基础数据集合
     * @return
     */
    private V offer(V value, Map<Object, V> dataTable) {
        Object key = identifier.getValue(value);
        V result = dataTable.put(key, value);
        return result;
    }

    /**
     * 索引基础数据处理
     * 
     * @param value 基础数据
     * @param indexTable 索引集合
     */
    private void index(V value, Map<String, List<Object>> indexTable) {
        for (IndexBuilder.IndexVisitor indexVisitor : indexVisitors.values()) {
            if (indexVisitor.indexable(value)) {
                String indexKey = indexVisitor.getIndexKey(value);
                addToIndexList(indexKey, value, indexTable);
            }
        }
    }

    /**
     * 索引基础数据
     * 
     * @param indexKey 索引字段值域的组合键值
     * @param value 基础数据
     * @param indexTable 索引集合
     */
    private void addToIndexList(String indexKey, V value, Map<String, List<Object>> indexTable) {
        List<Object> idList = indexTable.get(indexKey);
        if (idList == null) {
            idList = new ArrayList<Object>();
            indexTable.put(indexKey, idList);
        }
        Object id = identifier.getValue(value);
        idList.add(id);
    }

    /**
     * 排序
     * 
     * @param idList ID列表
     * @param indexTable 索引集合
     * @param dataTable 基础数据集合
     */
    private void sort(List<Object> idList, Map<String, List<Object>> indexTable,
            final Map<Object, V> dataTable) {
        Comparator<Object> comparator = null;
        if (Comparable.class.isAssignableFrom(clazz)) {
            comparator = new Comparator<Object>() {
                @SuppressWarnings({"rawtypes", "unchecked"})
                public int compare(Object o1, Object o2) {
                    Comparable entity1 = (Comparable) dataTable.get(o1);
                    Comparable entity2 = (Comparable) dataTable.get(o2);
                    return entity1.compareTo(entity2);
                }
            };
        } else {
            comparator = new Comparator<Object>() {
                @SuppressWarnings({"rawtypes", "unchecked"})
                public int compare(Object o1, Object o2) {
                    if (((o1 instanceof Comparable)) && ((o2 instanceof Comparable))) {
                        Comparable co1 = (Comparable) o1;
                        Comparable co2 = (Comparable) o2;
                        return co1.compareTo(co2);
                    }
                    return -1;
                }
            };
        }
        
        //索引排序
        for (List<Object> indexedIdList : indexTable.values()) {
            if (indexedIdList != null && !indexedIdList.isEmpty()) {
                Collections.sort(indexedIdList, comparator);
            }
        }
        
        // id排序
        Collections.sort(idList, comparator);
    }
}
