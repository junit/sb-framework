package org.chinasb.common.jreloader.compiler.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chinasb.common.jreloader.compiler.Compiler;

/**
 * Abstract compiler.
 * 
 * @author zhujuan
 */
public abstract class AbstractCompiler implements Compiler {

    private static final Pattern PACKAGE_PATTERN = Pattern
            .compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

    private static final Pattern CLASS_PATTERN = Pattern
            .compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s+");

    public Class<?> compile(String code) {
        code = code.trim();
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String pkg;
        if (matcher.find()) {
            pkg = matcher.group(1);
        } else {
            pkg = "";
        }
        matcher = CLASS_PATTERN.matcher(code);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in " + code);
        }
        String className = pkg != null && pkg.length() > 0 ? pkg + "." + cls : cls;
        if (!code.endsWith("}")) {
            throw new IllegalStateException("The java code not endsWith \"}\", code: \n" + code
                    + "\n");
        }
        try {
            return doCompile(className, code);
        } catch (RuntimeException t) {
            throw t;
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to compile class, cause: " + t.getMessage()
                    + ", class: " + className + ", code: \n" + code + "\n, stack: " + t);
        }
    }

    protected abstract Class<?> doCompile(String name, String source) throws Throwable;

}
