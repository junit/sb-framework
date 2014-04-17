package org.chinasb.common.rhino;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * js执行引擎
 * @author zhujuan
 */
public class Rhino {
    private static final ThreadLocal<Scriptable> THREAD_LOCALS = new ThreadLocal<Scriptable>();
    private static final LoadingCache<String, Script> SCRIPT_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000).build(new CacheLoader<String, Script>() {
                @Override
                public Script load(String expression) {
                    Context context = Context.enter();
                    Script exp = context.compileString(expression, "<expr>", -1, null);
                    return exp;
                }
            });

    /**
     * 获得当前线程的脚本作用域
     * @return
     */
    private static Scriptable getScope() {
        Scriptable scope = (Scriptable) THREAD_LOCALS.get();
        if (scope == null) {
            ContextFactory global = ContextFactory.getGlobal();
            Context context = global.enterContext();
            context.setWrapFactory(new WrapFactory() {
                protected JavascriptWrapper coreWrapper = new CoreJavaScriptWrapper();
                
                /**
                 * wrapper an java object to javascript object.
                 */
                public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
                    Object object = coreWrapper.wrap(cx, scope, obj, staticType);
                    if (object != obj) {
                        return object;
                    }
                    return super.wrap(cx, scope, obj, staticType);
                }
            });
            scope = context.initStandardObjects();
            THREAD_LOCALS.set(scope);
        }
        return scope;
    }

    /**
     * 执行脚本
     * @param expression
     * @param ctx
     * @return
     */
    public static Object invoke(String expression, Map<String, ?> ctx) {
        Script exp = SCRIPT_CACHE.getUnchecked(expression);
        Context context = Context.enter();
        try {
            Scriptable args = context.newObject(getScope());
            if ((ctx != null) && (!ctx.isEmpty())) {
                for (Map.Entry<String, ?> e : ctx.entrySet()) {
                    args.put((String) e.getKey(), args, e.getValue());
                }
            }
            return exp.exec(context, args);
        } finally {
            Context.exit();
        }
    }
}
