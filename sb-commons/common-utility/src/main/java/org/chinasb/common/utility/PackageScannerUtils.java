package org.chinasb.common.utility;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * 包类扫描工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class PackageScannerUtils {

    private static final Logger logger = LoggerFactory.getLogger(PackageScannerUtils.class);

    private static final ResourcePatternResolver resourcePatternResolver =
            new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(resourcePatternResolver);
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static Collection<Class<?>> scanPackages(String[] packageNames) {
        Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();

        for (String packageName : packageNames) {
            try {
                String packageSearchPath =
                        "classpath*:" + resolveBasePackage(packageName) + "/"
                                + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

                for (Resource resource : resources) {
                    String className = "";
                    try {
                        if (resource.isReadable()) {
                            MetadataReader metaReader =
                                    metadataReaderFactory.getMetadataReader(resource);
                            className = metaReader.getClassMetadata().getClassName();

                            Class<?> clazz = Class.forName(className);
                            clazzCollection.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("类 {} 不存在!", className);
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                logger.error("扫描包 {} 出错!", packageName);
                throw new RuntimeException(e);
            }
        }

        return clazzCollection;
    }

    private static String resolveBasePackage(String basePackage) {
        String placeHolderReplace = SystemPropertyUtils.resolvePlaceholders(basePackage);
        return ClassUtils.convertClassNameToResourcePath(placeHolderReplace);
    }
}
