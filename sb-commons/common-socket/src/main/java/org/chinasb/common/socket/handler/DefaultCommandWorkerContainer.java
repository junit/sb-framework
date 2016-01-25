package org.chinasb.common.socket.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.chinasb.common.jreloader.JReloader;
import org.chinasb.common.jreloader.JReloader.BaseReloader;
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
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 一个默认的指令工作器容器
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
    private JReloader jReloader;
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
        final int reloadModuleId = commandWorkerMeta.getReloadModuleId();
        if (reloadModuleId > 0) {
            jReloader.addReloader(reloadModuleId, new BaseReloader() {

                @Override
                public void onReload(Class<?> clazz) {
                    String className =
                            StringUtils.uncapitalize(StringUtils.unqualify(clazz.getName()));
                    AutowireCapableBeanFactory factory =
                            applicationContext.getAutowireCapableBeanFactory();
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClass(clazz);
                    beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                    beanDefinition.setAutowireCandidate(true);
                    registry.registerBeanDefinition(className, beanDefinition);
                    factory.getBean(clazz);
                }

                @Override
                public int getMoudle() {
                    return reloadModuleId;
                }
            });
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
    @SuppressWarnings("unchecked")
    public <T> T getWorker(Class<T> workerClazz) {
        String[] names = applicationContext.getBeanNamesForType(workerClazz);
        if ((names != null) && (names.length > 0)) {
            if (names.length == 1) {
                return (T) applicationContext.getBean(names[0]);
            }
            LOGGER.error("interface class[{}] too many implements bound !!", workerClazz);
        } else {
            LOGGER.error("bean or interface class[{}] NOT bound !!", workerClazz);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            throw new NullPointerException("applicationContext is null.");
        }
        this.applicationContext = applicationContext;
    }
}
