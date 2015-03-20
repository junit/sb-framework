package org.chinasb.common.utility;

/**
 * This class contains utilities that are used when we are working with classes
 * 
 * @author SoulKeeper
 */
public class ClassUtility
{
	/**
	 * Return true if class a is either equivalent to class b, or if class a is a subclass of class b, i.e. if a either
	 * "extends" or "implements" b. Note tht either or both "Class" objects may represent interfaces.
	 * 
	 * @param a
	 *            class
	 * @param b
	 *            class
	 * @return true if a == b or a extends b or a implements b
	 */
	@SuppressWarnings("unchecked")
	public static boolean isSubclass(Class<?> a, Class<?> b)
	{
		// We rely on the fact that for any given java class or
		// primtitive type there is a unqiue Class object, so
		// we can use object equivalence in the comparisons.
		if(a == b)
		{
			return true;
		}
		if(a == null || b == null)
		{
			return false;
		}
		for(Class x = a; x != null; x = x.getSuperclass())
		{
			if(x == b)
			{
				return true;
			}
			if(b.isInterface())
			{
				Class[] interfaces = x.getInterfaces();
				for(Class anInterface : interfaces)
				{
					if(isSubclass(anInterface, b))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if class in member of the package
	 * 
	 * @param clazz
	 *            class to check
	 * @param packageName
	 *            package
	 * @return true if is member
	 */
	public static boolean isPackageMember(Class<?> clazz, String packageName)
	{
		return isPackageMember(clazz.getName(), packageName);
	}

	/**
	 * Checks if classNames belongs to package
	 * 
	 * @param className
	 *            class name
	 * @param packageName
	 *            package
	 * @return true if belongs
	 */
	public static boolean isPackageMember(String className, String packageName)
	{
		if(!className.contains("."))
		{
			return packageName == null || packageName.isEmpty();
		}
		else
		{
			String classPackage = className.substring(0, className.lastIndexOf('.'));
			return packageName.equals(classPackage);
		}
	}
}
