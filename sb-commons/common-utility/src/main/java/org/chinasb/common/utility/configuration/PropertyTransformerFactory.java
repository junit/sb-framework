package org.chinasb.common.utility.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import org.chinasb.common.utility.ClassUtils;
import org.chinasb.common.utility.configuration.transformers.BooleanTransformer;
import org.chinasb.common.utility.configuration.transformers.ByteTransformer;
import org.chinasb.common.utility.configuration.transformers.CharTransformer;
import org.chinasb.common.utility.configuration.transformers.ClassTransformer;
import org.chinasb.common.utility.configuration.transformers.DoubleTransformer;
import org.chinasb.common.utility.configuration.transformers.EnumTransformer;
import org.chinasb.common.utility.configuration.transformers.FileTransformer;
import org.chinasb.common.utility.configuration.transformers.FloatTransformer;
import org.chinasb.common.utility.configuration.transformers.InetSocketAddressTransformer;
import org.chinasb.common.utility.configuration.transformers.IntegerTransformer;
import org.chinasb.common.utility.configuration.transformers.LongTransformer;
import org.chinasb.common.utility.configuration.transformers.PatternTransformer;
import org.chinasb.common.utility.configuration.transformers.ShortTransformer;
import org.chinasb.common.utility.configuration.transformers.StringTransformer;

/**
 * This class is responsible for creating property transformers. Each time it creates new instance
 * of custom property transformer, but for build-in it uses shared instances to avoid overhead
 * 
 * @author SoulKeeper
 */
public class PropertyTransformerFactory {
    /**
     * Returns property transformer or throws
     * {@link org.chinasb.common.utility.configuration.TransformationException} if can't create new
     * one.
     * 
     * @param clazzToTransform Class that will is going to be transformed
     * @param tc {@link org.chinasb.common.utility.configuration.PropertyTransformer} class that
     *        will be instantiated
     * @return instance of PropertyTransformer
     * @throws TransformationException if can't instantiate
     *         {@link org.chinasb.common.utility.configuration.PropertyTransformer}
     */
    public static PropertyTransformer<?> newTransformer(Class<?> clazzToTransform,
            Class<? extends PropertyTransformer> tc) throws TransformationException {

        // Just a hack, we can't set null to annotation value
        if (tc == PropertyTransformer.class) tc = null;

        if (tc != null)
            try {
                return tc.getConstructor().newInstance();
            } catch (Exception e) {
                throw new TransformationException("Can't instantiate property transfromer", e);
            }
        else {
            if (clazzToTransform == Boolean.class || clazzToTransform == Boolean.TYPE)
                return BooleanTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Byte.class || clazzToTransform == Byte.TYPE)
                return ByteTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Character.class || clazzToTransform == Character.TYPE)
                return CharTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Double.class || clazzToTransform == Double.TYPE)
                return DoubleTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Float.class || clazzToTransform == Float.TYPE)
                return FloatTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Integer.class || clazzToTransform == Integer.TYPE)
                return IntegerTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Long.class || clazzToTransform == Long.TYPE)
                return LongTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Short.class || clazzToTransform == Short.TYPE)
                return ShortTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == String.class)
                return StringTransformer.SHARED_INSTANCE;
            else if (clazzToTransform.isEnum())
                return EnumTransformer.SHARED_INSTANCE;
            // TODO: Implement Map
            // else if (ClassUtils.isSubclass(clazzToTransform, Map.class))
            // return MapTransformer.SHARED_INSTANCE;
            // else if (ClassUtils.isSubclass(clazzToTransform, Collection.class))
            // return CollectionTransformer.SHARED_INSTANCE;
            // else if (clazzToTransform.isArray())
            // return ArrayTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == File.class)
                return FileTransformer.SHARED_INSTANCE;
            else if (ClassUtils.isSubclass(clazzToTransform, InetSocketAddress.class))
                return InetSocketAddressTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Pattern.class)
                return PatternTransformer.SHARED_INSTANCE;
            else if (clazzToTransform == Class.class)
                return ClassTransformer.SHARED_INSTANCE;
            else
                throw new TransformationException("Transformer not found for class "
                        + clazzToTransform.getName());
        }
    }
}
