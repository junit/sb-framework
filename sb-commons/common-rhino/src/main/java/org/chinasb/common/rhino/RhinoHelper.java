package org.chinasb.common.rhino;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * 表达式帮助类
 * @author zhujuan
 */
public class RhinoHelper {
    private static Logger log = LoggerFactory.getLogger(RhinoHelper.class);

    /**
     * 执行表达式
     * @param expression
     * @param context
     * @return
     */
    private static Object execute(String expression, Map<String, ?> context) {
        if (!Strings.isNullOrEmpty(expression)) {
            try {
                return Rhino.invoke(expression, context);
            } catch (Exception ex) {
                log.error("公式: [{}], 参数[{}]执行错误 - ", expression, context);
                log.error("", ex);
            }
        }
        return null;
    }
    
    /**
     * 执行逻辑表达式
     * @param expression
     * @param ctx
     * @return
     */
    public static Boolean invoke(String expression, Map<String, ?> ctx) {
        Object value = execute(expression, ctx);
        return (Boolean) (value == null ? Boolean.valueOf(false) : value);
    }

    /**
     * 执行数值表达式
     * @param expression
     * @param numbers
     * @return
     */
    public static Number invoke(String expression, Number... numbers) {
        int len = numbers.length;
        Map<String, Object> ctx = new HashMap<String, Object>();
        for (int i = 0; i < len; i++) {
            ctx.put("n" + (i + 1), numbers[i]);
        }
        Object value = execute(expression, ctx);
        return value == null ? Integer.valueOf(0) : (Number) value;
    }
}
