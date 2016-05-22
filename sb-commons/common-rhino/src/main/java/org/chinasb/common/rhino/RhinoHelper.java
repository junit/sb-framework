package org.chinasb.common.rhino;

import java.util.HashMap;
import java.util.Map;

import org.chinasb.common.utility.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * 公式帮助类
 * 
 * @author zhujuan
 */
public class RhinoHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(RhinoHelper.class);

    /**
     * 执行公式表达式
     * 
     * @param expression 公式表达式
     * @param ctx 公式执行上下文
     * @return {@link Number}	公式表达式执行结果
     */
    private static Object execute(String expression, Map<String, ?> ctx) {
        if (!Strings.isNullOrEmpty(expression)) {
            try {
                return Rhino.invoke(expression, ctx);
            } catch (Exception ex) {
                LOGGER.error("公式: [{}], 参数[{}]执行错误 - ", expression, ctx);
                LOGGER.error("", ex);
            }
        }
        return null;
    }

    /**
     * 执行公式表达式
     * 
     * @param expression 公式表达式
     * @param ctx 公式执行上下文
     * @return {@link Number}	公式表达式执行结果
     */
	public static Number invoke(String expression, Map<String, ?> ctx) {
		Object value = execute(expression, ctx);
		return value == null ? 0 : (Number) value;
	}

	/**
	 * 执行公式表达式
	 * 
	 * @param expression 		公式表达式
	 * @param ctx 				公式执行上下文
	 * @param resultType 		执行结果类型
	 * @return T				公式表达式执行结果
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(String expression, Map<String, ?> ctx, Class<T> resultType) {
		Object value = execute(expression, ctx);
		if(resultType == Boolean.class || resultType == boolean.class) {
			return (T) (value == null ? false : value);
		} else {
			Number numberValue = value == null ? 0 : (Number) value;
			return NumberUtils.valueOf(resultType, numberValue);
		}
	}
	
    /**
     * 执行数值表达式
     * 
     * @param expression 公式表达式
     * @param numbers 公式参数
     * @return {@link Number}	公式表达式执行结果
     */
    public static Number invoke(String expression, Number... numbers) {
        int len = numbers.length;
        Map<String, Object> ctx = new HashMap<String, Object>();
        for (int i = 0; i < len; i++) {
            ctx.put("n" + (i + 1), numbers[i]);
        }
        Object value = execute(expression, ctx);
        return value == null ? 0 : (Number) value;
    }
    
    /**
	 * 执行公式表达式
	 * 
	 * @param  expression 		公式表达式
	 * @param  numbers 			公式参数
	 * @param  resultType 		执行结果类型
	 * @return T				公式表达式执行结果
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(String expression, Class<T> resultType, Number... numbers) {
		int len = numbers.length;
		Map<String, Object> ctx = new HashMap<String, Object>();
		for (int i = 0; i < len; i++) {
			ctx.put("n" + (i + 1), numbers[i]);
		}
		
		Object value = execute(expression, ctx);
		if(resultType == Boolean.class || resultType == boolean.class) {
			return (T) (value == null ? false : value);
		} else {
			Number numberValue = value == null ? 0 : (Number) value;
			return NumberUtils.valueOf(resultType, numberValue);
		}
	}
}
