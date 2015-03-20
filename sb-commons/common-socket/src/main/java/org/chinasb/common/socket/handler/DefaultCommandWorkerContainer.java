package org.chinasb.common.socket.handler;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.chinasb.common.NamedThreadFactory;
import org.chinasb.common.jreloader.JComplier;
import org.chinasb.common.jreloader.JReLoader;
import org.chinasb.common.jreloader.watcher.FolderWatcher;
import org.chinasb.common.jreloader.watcher.WatchEventListener;
import org.chinasb.common.socket.handler.Interceptor.Interceptor;
import org.chinasb.common.socket.handler.annotation.CommandInterceptor;
import org.chinasb.common.socket.handler.annotation.CommandMapping;
import org.chinasb.common.socket.handler.annotation.CommandWorker;
import org.chinasb.common.socket.handler.annotation.interceptors.ClassInterceptors;
import org.chinasb.common.socket.handler.annotation.interceptors.MethodInterceptors;
import org.chinasb.common.socket.handler.configuration.CommandInterceptorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 功能模块容器
 * 
 * @author zhujuan
 */
@Component
public class DefaultCommandWorkerContainer implements CommandWorkerContainer,
        ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DefaultCommandWorkerContainer.class);

    private List<Interceptor> globalInterceptors;

    @Autowired
    private CommandWorkerMeta commandWorkerMeta;
    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initialize() throws Exception {
        if (commandWorkerMeta.globalInterceptors().size() > 0) {
            this.globalInterceptors = new ArrayList<Interceptor>();
            for (CommandInterceptorConfig commandInterceptorConfig : commandWorkerMeta
                    .globalInterceptors()) {
                Class<Interceptor> clz =
                        (Class<Interceptor>) Thread.currentThread().getContextClassLoader()
                                .loadClass(commandInterceptorConfig.getClassName());
                if (clz == null) {
                    LOGGER.error("Error in initialize, Class["
                            + commandInterceptorConfig.getClassName() + "] NULL");
                    continue;
                }
                Interceptor interceptorInstance = getWorker(clz);
                if (interceptorInstance == null) {
                    LOGGER.error("Error in initialize, Instantiation["
                            + commandInterceptorConfig.getClassName() + "]");
                    continue;
                }
                globalInterceptors.add(interceptorInstance);
            }
        }
        // enable class reload
        if (commandWorkerMeta.isReloadable()) {
            final String reloadableDirectory = commandWorkerMeta.getDirectory();
            final JComplier complier = new JComplier(reloadableDirectory);
            FolderWatcher watcher = new FolderWatcher(reloadableDirectory);
            watcher.addWatchEventListener(new WatchEventListener() {

                @Override
                public void onWatchEvent(String dir, Path file, WatchEvent.Kind kind) {
                    File javaFile = file.toFile();
                    if (javaFile.exists()) {
                        String fileName =
                                javaFile.getName().substring(0, javaFile.getName().indexOf("."));
                        try {
                            String packageName = null;
                            StringBuffer javaSource = new StringBuffer();
                            List<String> lines = FileUtils.readLines(javaFile, "UTF-8");
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i);
                                if (packageName == null && line.indexOf("package") != -1) {
                                    packageName =
                                            line.substring(line.indexOf(" "), line.length() - 1)
                                                    .trim();
                                }
                                javaSource.append(line);
                            }
                            boolean success = complier.compile(fileName, javaSource.toString());
                            if (success) {
                                JReLoader loader = new JReLoader(reloadableDirectory);
                                Class<?> clazz =
                                        loader.loadClass(packageName == null ? fileName
                                                : packageName + "." + fileName);
                                if (clazz != null) {
                                    analyzeClass(clazz);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("error in reload class:" + file, e);
                        }
                    }
                }

                @Override
                public boolean support(Path file, Kind kind) {
                    return checkEndsWith(file.toString(), ".java", false);
                }

                private boolean checkEndsWith(String str, String end, boolean ingoreCase) {
                    int endLen = end.length();
                    return str.regionMatches(ingoreCase, str.length() - endLen, end, 0, endLen);
                }
            });
            NamedThreadFactory factory = new NamedThreadFactory("功能模块脚本重载线程", true);
            Thread thread = factory.newThread(watcher);
            thread.start();
        }
    }

    @Override
    public Map<Integer, CommandResolver> analyzeClass(Class<?> clazz) {
        Map<Integer, CommandResolver> resolvers = new HashMap<Integer, CommandResolver>();
        CommandWorker commandWorker = clazz.getAnnotation(CommandWorker.class);
        if (commandWorker != null) {
            try {
                // analyze class
                List<Interceptor> classInterceptorList = null;
                ClassInterceptors classInterceptors = clazz.getAnnotation(ClassInterceptors.class);
                if (classInterceptors != null) {
                    CommandInterceptor[] interceptors = classInterceptors.value();
                    if (interceptors != null && interceptors.length > 0) {
                        classInterceptorList = new ArrayList<Interceptor>();
                        for (CommandInterceptor interceptor : interceptors) {
                            Interceptor interceptorInstance = getWorker(interceptor.value());
                            if (interceptorInstance == null) {
                                LOGGER.error("Error in instantiation, analyzeClass[" + clazz
                                        + "], ClassInterceptor[" + interceptor.value() + "]");
                                continue;
                            }
                            classInterceptorList.add(interceptorInstance);
                        }
                    }
                }
                // analyze method
                Method[] methods = clazz.getDeclaredMethods();
                for (Method m : methods) {
                    CommandMapping commandMapping = m.getAnnotation(CommandMapping.class);
                    if (commandMapping != null) {
                        List<Interceptor> methodInterceptorList = null;
                        MethodInterceptors methodInterceptors =
                                m.getAnnotation(MethodInterceptors.class);
                        if (methodInterceptors != null) {
                            CommandInterceptor[] interceptors = methodInterceptors.value();
                            if (interceptors != null && interceptors.length > 0) {
                                methodInterceptorList = new ArrayList<Interceptor>();
                                for (CommandInterceptor interceptor : interceptors) {
                                    Interceptor interceptorInstance =
                                            getWorker(interceptor.value());
                                    if (interceptorInstance == null) {
                                        LOGGER.error("Error in instantiation, analyzeClass["
                                                + clazz + "], MethodInterceptor["
                                                + interceptor.value() + "]");
                                        continue;
                                    }
                                    methodInterceptorList.add(interceptorInstance);
                                }
                            }
                        }
                        Object instance = getWorker(clazz);
                        if (instance == null) {
                            LOGGER.error("Error in instantiation, analyzeClass[" + clazz + "]");
                            continue;
                        }
                        resolvers.put(
                                commandMapping.cmd(),
                                new DefaultCommandResolver(commandWorker.module(), commandMapping
                                        .cmd(), commandMapping.description(), m, instance,
                                        globalInterceptors, classInterceptorList,
                                        methodInterceptorList));

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error in analyzeClass[" + clazz + "]", e);
            }
        }
        return resolvers;
    }

    @Override
    public <T> T getWorker(Class<T> workerClazz) {
        return getBean(workerClazz);
    }
    
    /**
     * 获取Spring bean
     * @param beanClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T getBean(Class<T> beanClazz) {
        String[] names = applicationContext.getBeanNamesForType(beanClazz);
        if ((names != null) && (names.length > 0)) {
            if (names.length == 1) {
                return (T) applicationContext.getBean(names[0]);
            }
            LOGGER.error("interface class[{}] too many implements bound !!", beanClazz);
        } else {
            LOGGER.error("bean or interface class[{}] NOT bound !!", beanClazz);
        }
        return null;
    }
    
//    private <T> T getSimpleInstance(Class<T> workerClazz) {
//        if (workerClazz == null) {
//            return null;
//        }
//        T o;
//        try {
//            o = workerClazz.newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return o;
//    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            throw new NullPointerException("applicationContext is null.");
        }
        this.applicationContext = applicationContext;
    }
}
