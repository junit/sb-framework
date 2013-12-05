package org.chinasb.common.utility.configuration.transformers;

import java.lang.reflect.Field;

import org.chinasb.common.utility.configuration.PropertyTransformer;
import org.chinasb.common.utility.configuration.TransformationException;

/**
 * Transforms value that represents long to long. Value can be in decimal or hex format.
 */
public class LongTransformer implements PropertyTransformer<Long> {
    /**
     * Shared instance of this transformer. It's thread-safe so no need of multiple instances
     */
    public static final LongTransformer SHARED_INSTANCE = new LongTransformer();

    /**
     * Transforms value to long
     * 
     * @param value
     *            value that will be transformed
     * @param field
     *            value will be assigned to this field
     * @return Long that represents value
     * @throws TransformationException
     *             if something went wrong
     */
    @Override
    public Long transform(String value, Field field) throws TransformationException {
        try {
            return Long.decode(value);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }
}
