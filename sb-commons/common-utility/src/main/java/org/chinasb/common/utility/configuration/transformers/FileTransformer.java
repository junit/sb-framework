package org.chinasb.common.utility.configuration.transformers;

import java.io.File;
import java.lang.reflect.Field;

import org.chinasb.common.utility.configuration.PropertyTransformer;
import org.chinasb.common.utility.configuration.TransformationException;

/**
 * Transforms string to file by creating new file instance. It's not checked if file exists.
 * 
 * @author SoulKeeper
 */
public class FileTransformer implements PropertyTransformer<File>
{
	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final FileTransformer	SHARED_INSTANCE	= new FileTransformer();

	/**
	 * Transforms String to the file
	 * 
	 * @param value
	 *            value that will be transformed
	 * @param field
	 *            value will be assigned to this field
	 * @return File object that represents string
	 */
	@Override
	public File transform(String value, Field field) throws TransformationException
	{
		return new File(value);
	}
}
