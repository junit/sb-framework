package org.chinasb.common.basedb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chinasb.common.basedb.annotation.Index;
import org.chinasb.common.rhino.RhinoHelper;
import org.chinasb.common.utility.BeanUtility;
import org.chinasb.common.utility.ReflectionUtility;

import com.google.common.base.Objects;

/**
 * 索引生成器
 * @author zhujuan
 */
public class IndexBuilder {

    /**
     * 创建索引访问器
     * @param clazz
     * @return
     */
    public static Map<String, IndexVisitor> createIndexVisitors(Class<?> clazz) {
        Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clazz, Index.class);
        Map<String, IndexVisitor> indexMap = new HashMap<String, IndexVisitor>();
        for (Field field : fields) {
            Index index = (Index) field.getAnnotation(Index.class);
            if (index != null) {
                IndexVisitor indexVisitor = (IndexVisitor) indexMap.get(index.name());
                if (indexVisitor == null) {
                    indexVisitor = new IndexVisitor(index.name());
                    indexMap.put(index.name(), indexVisitor);
                }
                indexVisitor.attachField(new Field[] {field});
            }
        }
        if (!indexMap.isEmpty()) {
            for (IndexVisitor indexVisitor : indexMap.values()) {
                List<Field> fieldList = indexVisitor.getFields();
                if ((fieldList != null) && (fieldList.size() > 1)) {
                    Collections.sort(fieldList, new Comparator<Field>() {
                        @Override
                        public int compare(Field f1, Field f2) {
                            Index index1 = (Index) f1.getAnnotation(Index.class);
                            Index index2 = (Index) f2.getAnnotation(Index.class);
                            return index1.order() < index2.order() ? -1 : 1;
                        }
                    });
                }
            }
        }
        return indexMap;
    }

    /**
     * 索引访问器
     * @author zhujuan
     */
    public static class IndexVisitor {
        private final String name;
        private final List<Field> fields = new ArrayList<Field>();
        private final List<String> expressions = new ArrayList<String>();

        public IndexVisitor(String indexName) {
            name = indexName;
        }

        /**
         * 附加字段
         * @param fieldList
         */
        public void attachField(Field... fieldList) {
            if ((fieldList != null) && (fieldList.length > 0)) {
                for (Field field : fieldList) {
                    fields.add(field);
                    Index index = (Index) field.getAnnotation(Index.class);
                    if ((index != null) && (index.expression() != null)
                            && (index.expression().trim().length() > 0)) {
                        expressions.add(index.expression().trim());
                    }
                }
            }
        }

        /**
         * 获得索引名称
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * 获得字段集合
         * @return
         */
        public List<Field> getFields() {
            return fields;
        }
        
        /**
         * 获得索引键名
         * @param obj
         * @return
         */
        public String getIndexKey(Object obj) {
            if (obj != null) {
                Object[] fieldValues = null;
                if ((fields != null) && (!fields.isEmpty())) {
                    fieldValues = new String[fields.size()];
                    for (int i = 0; i < fields.size(); i++) {
                        Field field = (Field) fields.get(i);
                        field.setAccessible(true);
                        Object fieldValue = ReflectionUtility.getField(field, obj);
                        if (fieldValue != null) {
                            fieldValues[i] = String.valueOf(fieldValue);
                        } else {
                            fieldValues[i] = "";
                        }
                    }
                }
                return KeyBuilder.buildIndexKey(obj.getClass(), name, fieldValues);
            }
            return null;
        }

        /**
         * 判断是否可以索引{非空对象和表达式正确}
         * @param obj
         * @return
         */
        public boolean indexable(Object obj) {
            if (obj != null) {
                if ((expressions != null) && (!expressions.isEmpty())) {
                    Map<String, Object> ctx = BeanUtility.buildMap(obj);
                    for (String expression : expressions) {
                        if (!(RhinoHelper.invoke(expression, ctx)).booleanValue()) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof IndexVisitor)) {
                return false;
            }
            IndexVisitor rhs = (IndexVisitor) o;
            return Objects.equal(name, rhs.name);
        }
    }

}
