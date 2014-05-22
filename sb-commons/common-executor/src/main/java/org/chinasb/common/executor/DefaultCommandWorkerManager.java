package org.chinasb.common.executor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.chinasb.common.executor.Interceptor.Interceptor;
import org.chinasb.common.executor.annotation.CommandInterceptor;
import org.chinasb.common.executor.annotation.CommandMapping;
import org.chinasb.common.executor.annotation.CommandWorker;
import org.chinasb.common.executor.annotation.interceptors.ClassInterceptors;
import org.chinasb.common.executor.annotation.interceptors.MethodInterceptors;
import org.chinasb.common.executor.configuration.CommandInterceptorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 指令工作器管理
 * @author zhujuan
 */
@Component
public class DefaultCommandWorkerManager implements CommandWorkerManager {
    private Map<String, CommandResolver> resolvers = new HashMap<String, CommandResolver>();
    private SortedMap<Integer, WildcardEntity> wildCardEntities =
            new ConcurrentSkipListMap<Integer, WildcardEntity>();

    @Autowired
    private CommandWorkerContainer commandWorkerContainer;
    @Autowired
    private CommandWorkerMeta commandWorkerMeta;
    private List<Interceptor> globalInterceptors;

    @PostConstruct
    protected void initialize() {
        if (commandWorkerMeta.globalInterceptors().size() > 0) {
            this.globalInterceptors = new ArrayList<Interceptor>();
            for (CommandInterceptorConfig commandInterceptorConfig : commandWorkerMeta
                    .globalInterceptors()) {
                try {
                    Class<Interceptor> clz =
                            (Class<Interceptor>) Thread.currentThread().getContextClassLoader()
                                    .loadClass(commandInterceptorConfig.getClassName());
                    globalInterceptors.add(commandWorkerContainer.getWorker(clz,
                            commandInterceptorConfig.isSpringBean()));
                } catch (Exception e) {
                    throw new RuntimeException("", e);
                }
            }
        }

        File root =
                new File(DefaultCommandWorkerManager.class.getClass().getResource("/").getFile());
        String prefix = root.getAbsolutePath();
        List<Class<?>> classes = new ArrayList<Class<?>>();
        listCommandClass(root, prefix, classes);
        for (Class<?> cls : classes) {
            analyzeClass(cls);
        }
    }

    private void analyzeClass(Class<?> clazz) {
        CommandWorker commandWorker = clazz.getAnnotation(CommandWorker.class);
        if (null != commandWorker) {
            try {
                // analyze class interceptors
                List<Interceptor> classInterceptorList = null;
                ClassInterceptors classInterceptors = clazz.getAnnotation(ClassInterceptors.class);
                if (null != classInterceptors) {
                    CommandInterceptor[] interceptors = classInterceptors.value();
                    if (null != interceptors && interceptors.length > 0) {
                        classInterceptorList = new ArrayList<Interceptor>();
                        for (CommandInterceptor interceptor : interceptors) {
                            classInterceptorList.add(commandWorkerContainer.getWorker(
                                    interceptor.value(), interceptor.isSpringBean()));
                        }
                    }
                }

                // analyze method interceptors
                Method[] methods = clazz.getDeclaredMethods();
                for (Method m : methods) {
                    CommandMapping commandMapping = m.getAnnotation(CommandMapping.class);
                    if (null != commandMapping) {
                        List<Interceptor> methodInterceptorList = null;
                        MethodInterceptors methodInterceptors =
                                m.getAnnotation(MethodInterceptors.class);
                        if (null != methodInterceptors) {
                            CommandInterceptor[] interceptors = methodInterceptors.value();
                            if (null != interceptors && interceptors.length > 0) {
                                methodInterceptorList = new ArrayList<Interceptor>();
                                for (CommandInterceptor interceptor : interceptors) {
                                    methodInterceptorList.add(commandWorkerContainer.getWorker(
                                            interceptor.value(), interceptor.isSpringBean()));
                                }
                            }
                        }

                        Class<?>[] paramTypes = m.getParameterTypes();
                        if ("".equals(commandWorker.workerName())) {
                            if (commandMapping.isWildcard()) {
                                wildCardEntities
                                        .put(commandMapping.weight(),
                                                new WildcardEntity(commandMapping.mapping(),
                                                        new DefaultCommandResolver(m, paramTypes,
                                                                clazz, commandWorkerContainer
                                                                        .getWorker(clazz, true),
                                                                globalInterceptors,
                                                                classInterceptorList,
                                                                methodInterceptorList)));
                            } else {
                                resolvers.put(commandMapping.mapping(),
                                        new DefaultCommandResolver(m, paramTypes, clazz,
                                                commandWorkerContainer.getWorker(clazz, true),
                                                globalInterceptors, classInterceptorList,
                                                methodInterceptorList));
                            }
                        } else {
                            if (commandMapping.isWildcard()) {
                                wildCardEntities.put(
                                        commandMapping.weight(),
                                        new WildcardEntity(commandMapping.mapping(),
                                                new DefaultCommandResolver(m, paramTypes, clazz,
                                                        commandWorkerContainer.getWorker(
                                                                commandWorker.workerName(), true),
                                                        globalInterceptors, classInterceptorList,
                                                        methodInterceptorList)));
                            } else {
                                resolvers.put(
                                        commandMapping.mapping(),
                                        new DefaultCommandResolver(m, paramTypes, clazz,
                                                commandWorkerContainer.getWorker(
                                                        commandWorker.workerName(), true),
                                                globalInterceptors, classInterceptorList,
                                                methodInterceptorList));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("error in analyzeClass", e);
            }
        }
    }

    private void listCommandClass(File file, String prefix, List<Class<?>> list) {
        File[] files = file.listFiles();
        if (null != files) {
            try {
                for (File f : files) {
                    if (f.isDirectory()) {
                        listCommandClass(f, prefix, list);
                    } else {
                        if (f.getName().endsWith(".class")) {
                            String parent = f.getParent();
                            String name = f.getName();
                            String classpath =
                                    (parent.substring(prefix.length() + 1) + File.separator + name
                                            .substring(0, name.length() - 6)).replace("\\", ".");
                            if (commandWorkerMeta.isScanPackage(classpath)) {
                                list.add(Class.forName(classpath.replace(File.separator, ".")));
                            }
                        } else if (f.getName().endsWith(".jar")) {
                            JarFile jarFile = new JarFile(f);
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()) {
                                JarEntry jarEntry = jarEntries.nextElement();
                                if (jarEntry.getName().endsWith(".class")) {

                                    String jarEntryName = jarEntry.getName();
                                    String classpath =
                                            jarEntryName.substring(0, jarEntryName.length() - 6)
                                                    .replace("/", ".");
                                    if (commandWorkerMeta.isScanPackage(classpath)) {
                                        list.add(Thread.currentThread().getContextClassLoader()
                                                .loadClass(classpath));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("error in listCommandClass", e);
            }
        }
    }

    @Override
    public CommandResolver getResolver(String command) {
        CommandResolver resolver = resolvers.get(command);
        if (null == resolver) {
            for (int weight : wildCardEntities.keySet()) {
                WildcardEntity entity = wildCardEntities.get(weight);
                if (entity.isMatch(command)) {
                    return entity.getResolver();
                }
            }
        }
        return resolver;
    }

    private class WildcardEntity {
        private Pattern p;
        private CommandResolver resolver;

        public WildcardEntity(String wildString, CommandResolver resolver) {
            this.p = Pattern.compile(wildString);
            this.resolver = resolver;
        }

        public boolean isMatch(String mapping) {
            return p.matcher(mapping).matches();
        }

        public CommandResolver getResolver() {
            return resolver;
        }
    }

}
