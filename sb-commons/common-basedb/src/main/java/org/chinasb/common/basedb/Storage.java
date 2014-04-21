package org.chinasb.common.basedb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.chinasb.common.basedb.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.ApplicationContext;

/**
 * 基础数据存储
 * @author zhujuan
 * @param <V>
 */
public class Storage<V> {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    /**
     * 基础数据的类对象
     */
    private Class<V> clazz;
    /**
     * 基础数据存放路径
     */
    private String location;
    /**
     * 资源读取器
     */
    private ResourceReader reader;
    /**
     * 唯一标识取值器
     */
    private Getter identifier;
    /**
     * 索引访问器映射集合
     */
    private Map<String, IndexBuilder.IndexVisitor> indexVisitors;
    /**
     * 基础数据映射集合Map<唯一标识, 基础数据>
     */
    private Map<Object, V> dataTable = new HashMap<Object, V>();
    /**
     * 索引数据映射集合
     */
    private Map<String, Object> indexTable = new HashMap<String, Object>();
    /**
     * 唯一标识集合
     */
    private List<Object> idList = new CopyOnWriteArrayList<Object>();
    private String resourceLocation = "basedb" + File.separator;
    private ApplicationContext applicationContext;

    /**
     * 构造一个基础数据仓库
     * @param clazz
     * @param resourceLocation
     * @param applicationContext
     */
    public Storage(Class<V> clazz, String resourceLocation, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.resourceLocation = resourceLocation;
        this.clazz = clazz;
        initialize(clazz);
    }

    /**
     * 基础数据仓库初始化
     * @param clazz
     */
    private void initialize(Class<V> clazz) {
        this.clazz = clazz;
        Resource resource = (Resource) clazz.getAnnotation(Resource.class);
        location = (resourceLocation + clazz.getSimpleName() + "." + resource.suffix());
        ResourceReader reader =
                (ResourceReader) applicationContext.getBean(resource.type() + "ResourceReader",
                        ResourceReader.class);
        this.reader = reader;
        identifier = GetterBuilder.createIdGetter(clazz);
        indexVisitors = IndexBuilder.createIndexVisitors(clazz);
    }

    /**
     * 获得基础数据映射集合
     * @return
     */
    public Map<Object, V> getDataTable() {
        return dataTable;
    }

    /**
     * 获得索引数据映射集合
     * @return
     */
    public Map<String, Object> getIndexTable() {
        return indexTable;
    }

    /**
     * 获得索引数据集合
     * @param indexName
     * @param indexValues
     * @return
     */
    public List<V> getIndex(String indexName, Object... indexValues) {
        List idList = getIndexIdList(indexName, indexValues);
        return list(idList);
    }

    /**
     * 获得索引数据的唯一标识集合
     * @param indexName
     * @param indexValues
     * @return
     */
    public List getIndexIdList(String indexName, Object... indexValues) {
        String indexkey = getIndexKey(indexName, indexValues);
        return (List) indexTable.get(indexkey);
    }

    /**
     * 获得基础数据
     * @param key
     * @return
     */
    public V get(Object key) {
        return dataTable.get(key);
    }

    /**
     * 获得基础数据集合
     * @param idList
     * @return
     */
    public List<V> list(List idList) {
        List<V> resultList = new ArrayList<V>();
        if ((idList != null) && (!idList.isEmpty())) {
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
     * 获得基础数据集合
     * @return
     */
    public Collection<V> listAll() {
        return new ArrayList<V>(dataTable.values());
    }

    /**
     * 基础数据重新加载
     */
    public synchronized void reload() {
        try {
            URL resource = getClass().getClassLoader().getResource(location);
            if (resource == null) {
                FormattingTuple message =
                        MessageFormatter.format("基础数据[{}]所对应的资源文件[{}]不存在!", clazz.getName(),
                                location);
                logger.error(message.getMessage());
                return;
            }
            InputStream input = resource.openStream();
            Iterator<V> it = reader.read(input, clazz);

            List<Object> idList_copy = new ArrayList<Object>();
            Map<Object, V> dataTable_copy = new HashMap<Object, V>();
            Map<String, Object> indexTable_copy = new HashMap<String, Object>();
            while (it.hasNext()) {
                V obj = it.next();
                if ((obj instanceof InitializeBean)) {
                    try {
                        ((InitializeBean) obj).afterPropertiesSet();
                    } catch (Exception e) {
                        FormattingTuple message =
                                MessageFormatter.format("基础数据[{}] 属性设置后处理出错!", clazz.getName());
                        logger.error(message.getMessage(), e);
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
            logger.info("完成加载  {} 基础数据...", clazz.getName());
        } catch (IOException e) {
            FormattingTuple message =
                    MessageFormatter.format("基础数据[{}]所对应的资源文件[{}]不存在!", clazz.getName(), location);
            logger.error(message.getMessage());
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    /**
     * 获得索引键名
     * @param name
     * @param value
     * @return
     */
    private String getIndexKey(String name, Object... value) {
        return KeyBuilder.buildIndexKey(clazz, name, value);
    }

    /**
     * 增加基础数据
     * @param value
     * @param dataTable
     * @return
     */
    private V offer(V value, Map<Object, V> dataTable) {
        Object key = identifier.getValue(value);
        V result = dataTable.put(key, value);
        return result;
    }

    /**
     * 索引基础数据
     * @param value
     * @param indexTable
     */
    private void index(V value, Map<String, Object> indexTable) {
        for (IndexBuilder.IndexVisitor indexVisitor : indexVisitors.values()) {
            if (indexVisitor.indexable(value)) {
                String indexKey = indexVisitor.getIndexKey(value);
                addToIndexList(indexKey, value, indexTable);
            }
        }
    }

    /**
     * 增加基础数据索引
     * @param indexKey
     * @param value
     * @param indexTable
     */
    private void addToIndexList(String indexKey, V value, Map<String, Object> indexTable) {
        List idList = (List) indexTable.get(indexKey);
        if (idList == null) {
            idList = new ArrayList();
            indexTable.put(indexKey, idList);
        }
        Object id = identifier.getValue(value);
        idList.add(id);
    }
    
    /**
     * 对唯一标识集合和索引数据集合排序
     * @param idList
     * @param indexTable
     * @param dataTable
     */
    private void sort(List<Object> idList, Map<String, Object> indexTable,
            final Map<Object, V> dataTable) {
        Comparator<Object> comparator = null;
        if (Comparable.class.isAssignableFrom(clazz)) {
            comparator = new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    Comparable entity1 = (Comparable) dataTable.get(o1);
                    Comparable entity2 = (Comparable) dataTable.get(o2);
                    return entity1.compareTo(entity2);
                }
            };
        } else {
            comparator = new Comparator<Object>() {
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
        for (Object indexObj : indexTable.values()) {
            if ((indexObj instanceof List)) {
                List<?> indexedIdList = (List<?>) indexObj;
                if ((indexedIdList != null) && (indexedIdList.size() > 1)) {
                    Collections.sort(indexedIdList, comparator);
                }
            }
        }
        Collections.sort(idList, comparator);
    }
}
