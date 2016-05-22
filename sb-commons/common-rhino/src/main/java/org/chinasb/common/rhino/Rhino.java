package org.chinasb.common.rhino;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Mozilla Rhino 公式解析器
 * 
 * @author zhujuan
 */
public class Rhino {
    private static final ThreadLocal<Scriptable> THREAD_LOCALS = new ThreadLocal<Scriptable>();
    private static final LoadingCache<String, Script> SCRIPT_CACHE = CacheBuilder.newBuilder()
            .maximumSize(512).build(new CacheLoader<String, Script>() {
                @Override
                public Script load(String expression) {
                    Context context = Context.enter();
                    Script exp = context.compileString(expression, "<expr>", -1, null);
                    return exp;
                }
            });

    /**
     * 获得当前线程的脚本作用域
     * 
     * @return
     */
    private static Scriptable getScope() {
        Scriptable scope = THREAD_LOCALS.get();
        if (scope == null) {
            ContextFactory global = ContextFactory.getGlobal();
            Context context = global.enterContext();
            context.setWrapFactory(new MapWarperFactory());
            scope = context.initStandardObjects();
            THREAD_LOCALS.set(scope);
        }
        return scope;
    }

    /**
     * 计算公式
     * 
     * @param expression 公式
     * @param ctx 上下文
     * @return {@link Object}	返回值
     */
    public static Object invoke(String expression, Map<String, ?> ctx) {
        Script exp = SCRIPT_CACHE.getUnchecked(expression);
        Context context = Context.enter();
        try {
            Scriptable args = context.newObject(getScope());
            if (ctx != null && !ctx.isEmpty()) {
                for (Map.Entry<String, ?> e : ctx.entrySet()) {
                    args.put(e.getKey(), args, e.getValue());
                }
            }
            return exp.exec(context, args);
        } finally {
            Context.exit();
        }
    }
}
