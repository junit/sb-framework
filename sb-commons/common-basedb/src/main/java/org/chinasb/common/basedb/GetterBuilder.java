package org.chinasb.common.basedb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.chinasb.common.basedb.annotation.Id;
import org.chinasb.common.utility.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ReflectionUtils;

/**
 * 取值构建器
 * 
 * @author zhujuan
 */
public class GetterBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetterBuilder.class);

    /**
     * 创建指定资源类的ID取值器
     * 
     * @param clz 资源类
     * @return
     */
    public static Getter createIdGetter(Class<?> clz) {
        Field[] fields = ReflectionHelper.getDeclaredFieldsWith(clz, Id.class);
        if (fields == null) {
            FormattingTuple message = MessageFormatter.format("类 [{}] 缺少唯一标识声明", clz);
            LOGGER.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
        if (fields.length > 1) {
            FormattingTuple message = MessageFormatter.format("类 [{}] 有多个唯一标识声明", clz);
            LOGGER.error(message.getMessage());
            throw new RuntimeException(message.getMessage());
        }
        return new FieldGetter(fields[0]);
    }

    /**
     * 属性取值器
     * 
     * @author zhujuan
     */
    private static class FieldGetter implements Getter {
        private final Field field;

        public FieldGetter(Field field) {
            ReflectionUtils.makeAccessible(field);
            this.field = field;
        }

        @Override
        public Object getValue(Object object) {
            Object value = null;
            try {
                value = field.get(object);
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("标识符属性访问异常", e);
                GetterBuilder.LOGGER.error(message.getMessage());
                throw new RuntimeException(message.getMessage());
            }
            return value;
        }
    }

    /**
     * 方法取值器
     * 
     * @author zhujuan
     */
    @SuppressWarnings("unused")
    private static class MethodGetter implements Getter {
        private final Method method;

        public MethodGetter(Method method) {
            ReflectionUtils.makeAccessible(method);
            this.method = method;
        }

        @Override
        public Object getValue(Object object) {
            Object value = null;
            try {
                value = method.invoke(object, new Object[0]);
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("标识方法访问异常", e);
                GetterBuilder.LOGGER.error(message.getMessage());
                throw new RuntimeException(message.getMessage());
            }
            return value;
        }
    }
}
