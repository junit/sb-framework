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
 * 类包工具类
 * @author zhujuan
 */
public class PackageUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageUtility.class);
    private static final ResourcePatternResolver resourcePatternResolver =
            new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(resourcePatternResolver);
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 扫描类包
     * @param packageNames
     * @return
     */
    public static Collection<Class<?>> scanPackages(String... packageNames) {
        Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();

        String[] arrayOfString = packageNames;
        int j = packageNames.length;
        for (int i = 0; i < j; i++) {
            String packageName = arrayOfString[i];
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
                        LOGGER.error("类 {} 不存在!", className);
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("扫描包 {} 出错!", packageName);
                throw new RuntimeException(e);
            }
        }
        return clazzCollection;
    }

    /**
     * 解析类包路径
     * @param basePackage
     * @return
     */
    private static String resolveBasePackage(String basePackage) {
        String placeHolderReplace = SystemPropertyUtils.resolvePlaceholders(basePackage);
        return ClassUtils.convertClassNameToResourcePath(placeHolderReplace);
    }
}
