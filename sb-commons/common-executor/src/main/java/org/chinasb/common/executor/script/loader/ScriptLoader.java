package org.chinasb.common.executor.script.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义类加载器
 * @author zhujuan
 */
public class ScriptLoader extends ClassLoader {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ScriptLoader.class);

	private String classPath;

	public ScriptLoader(String classPath) {
		this.classPath = classPath;
	}

	@Override
	public Class<?> loadClass(String name) {
		return findClass(name);
	}

	@Override
	public Class<?> findClass(String name) {
		try {
			byte[] byteCode = loadByteCode(name);
			return super.defineClass(name, byteCode, 0, byteCode.length);
		} catch (IOException e) {
			try {
				return super.loadClass(name);
			} catch (ClassNotFoundException ignore) {
			}
			LOGGER.error("加载类出错：", e);
			return null;
		}
	}

	/**
	 * 加载字节码
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private byte[] loadByteCode(String name) throws IOException {
		String classFileName = classPath + File.separator
				+ name.replace('.', '/') + ".class";
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = new FileInputStream(classFileName);
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int iRead = 0;
			while ((iRead = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, iRead);
			}
			return bos.toByteArray();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {
				LOGGER.error("关闭文件输入流错误：", e);
			}
			try {
				if (bos != null)
					bos.close();
			} catch (Exception e) {
				LOGGER.error("关闭字节数组输出流错误：", e);
			}
		}
	}
}
