package org.chinasb.common.jreloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * JComplier
 * 
 * @author zhujuan
 *
 */
public class JComplier {

	private final JavaCompiler compiler;
	private final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	private final ClassReloader reloader;
	private final ClassFileManager fileManager;
	private volatile List<String> options;

	@SuppressWarnings("resource")
	public JComplier() {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new NullPointerException(
					"Couldnt find JDK compiler on path (JDK tools.jar)");
		options = new ArrayList<String>();
		options.add("-encoding");
		options.add("UTF-8");
		StandardJavaFileManager fm = compiler.getStandardFileManager(
				diagnostics, null, null);
		final ClassLoader loader = Thread.currentThread()
				.getContextClassLoader();
		if (loader instanceof URLClassLoader
				&& (!loader.getClass().getName()
						.equals("sun.misc.Launcher$AppClassLoader"))) {
			try {
				URLClassLoader urlClassLoader = (URLClassLoader) loader;
				List<File> files = new ArrayList<File>();
				for (URL url : urlClassLoader.getURLs()) {
					files.add(new File(url.getFile()));
				}
				fm.setLocation(StandardLocation.CLASS_PATH, files);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		reloader = AccessController
				.doPrivileged(new PrivilegedAction<ClassReloader>() {
					public ClassReloader run() {
						return new ClassReloader(loader);
					}
				});
		fileManager = new ClassFileManager(fm, reloader);
	}

	/**
	 * 编译
	 * @param canonicalClassName Java语言规范中定义的标准类名称(eg. xx.Sample)
	 * @param sourceCode Java类的源代码
	 * @return
	 * @throws Throwable
	 */
	public Class<?> compile(String canonicalClassName, String sourceCode)
			throws Throwable {
		int i = canonicalClassName.lastIndexOf('.');
		String packageName = i < 0 ? "" : canonicalClassName.substring(0, i);
		String className = i < 0 ? canonicalClassName : canonicalClassName
				.substring(i + 1);
		ComplexJavaFileObject javaFileObject = new ComplexJavaFileObject(className,
				sourceCode);
		fileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName,
				className + Kind.SOURCE.extension, javaFileObject);

		CompilationTask compilationTask = compiler.getTask(null, fileManager,
				diagnostics, options, null,
				Arrays.asList(new JavaFileObject[] { javaFileObject }));
		Boolean result = compilationTask.call();
		if (result == null || !result.booleanValue()) {
			StringBuffer sb = new StringBuffer();
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				sb.append("Source:[" + diagnostic.getSource() + "]\n");
				sb.append("Kind:[" + diagnostic.getKind() + "], ");
				sb.append("Code:[" + diagnostic.getCode() + "]\n");
				sb.append("Position:[" + diagnostic.getPosition() + "], ");
				sb.append("Start Position:[" + diagnostic.getStartPosition()
						+ "], ");
				sb.append("End Position:[" + diagnostic.getEndPosition()
						+ "]\n");
				sb.append("LineNumber:[" + diagnostic.getLineNumber() + "], ");
				sb.append("ColumnNumber:[" + diagnostic.getColumnNumber()
						+ "]\n");
				sb.append("Message:[" + diagnostic.getMessage(null) + "]\n");
			}
			throw new IllegalStateException("Compilation failed. class: "
					+ canonicalClassName + "\n" + sb.toString());
		}
		return reloader.loadClass(canonicalClassName);
	}

	/** {@link ClassLoader}的一个实现， */  
	private final class ClassReloader extends ClassLoader {

		private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

		ClassReloader(final ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		Collection<JavaFileObject> files() {
			return Collections.unmodifiableCollection(classes.values());
		}

		@Override
		protected Class<?> findClass(final String qualifiedClassName)
				throws ClassNotFoundException {
			JavaFileObject file = classes.get(qualifiedClassName);
			if (file != null) {
				byte[] bytes = ((ComplexJavaFileObject) file).getByteCode();
				return defineClass(qualifiedClassName, bytes, 0, bytes.length);
			}
			try {
				return Class.forName(qualifiedClassName);
			} catch (ClassNotFoundException nf) {
				return super.findClass(qualifiedClassName);
			}
		}

		void add(final String qualifiedClassName, final JavaFileObject javaFile) {
			classes.put(qualifiedClassName, javaFile);
		}

		@Override
		protected synchronized Class<?> loadClass(final String name,
				final boolean resolve) throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}

		@Override
		public InputStream getResourceAsStream(final String name) {
			if (name.endsWith(Kind.CLASS.extension)) {
				String qualifiedClassName = name.substring(0,
						name.length() - Kind.CLASS.extension.length()).replace(
						'/', '.');
				ComplexJavaFileObject file = (ComplexJavaFileObject) classes
						.get(qualifiedClassName);
				if (file != null) {
					return new ByteArrayInputStream(file.getByteCode());
				}
			}
			return super.getResourceAsStream(name);
		}
	}

	/** 
	 * {@link FileObject}和{@link JavaFileObject}的一个实现，它能持有java源代码或编译后的class。这个类可以用于： 
	 * <ol> 
	 * <li>存放需要传递给编译器的源码，这时使用的是{@link ComplexJavaFileObject#BaseJavaFileObject(String, CharSequence)}构造器。</li> 
	 * <li>存放编译器编译完的byte code，这是使用的是{@link ComplexJavaFileObject#BaseJavaFileObject(String, JavaFileObject.Kind)}</li> 
	 * </ol> 
	 */  
	private static final class ComplexJavaFileObject extends SimpleJavaFileObject {
		/** 如果kind == CLASS, 存储byte code，可以通过{@link #openInputStream()}得到 */  
		private ByteArrayOutputStream bytecode;
		/** 如果kind == SOURCE, 存储源码 */  
		private final CharSequence source;

		/**
		 * 创建持有源码的实例
		 * @param name
		 * @param source
		 */
		ComplexJavaFileObject(final String name, final CharSequence source) {
			super(URI.create(name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source = source;
		}

		ComplexJavaFileObject(final String name, final Kind kind) {
			super(URI.create(name), kind);
			source = null;
		}

		ComplexJavaFileObject(final URI uri, final Kind kind) {
			super(uri, kind);
			source = null;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
				throws UnsupportedOperationException {
			if (source == null) {
				throw new UnsupportedOperationException("source == null");
			}
			return source;
		}

		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(getByteCode());
		}

		@Override
		public OutputStream openOutputStream() {
			return bytecode = new ByteArrayOutputStream();
		}

		public byte[] getByteCode() {
			return bytecode.toByteArray();
		}
	}

	/** 
	* {@link JavaFileManager}的一个实例，用于管理Java源代码和byte code。<br> 
	* 所有的源码以{@link CharSequence}的形式保存在内存中，byte code以byte数组形式存放在内存中。 
	*/  
	private static final class ClassFileManager extends
			ForwardingJavaFileManager<JavaFileManager> {

		private final ClassReloader classReloader;

		private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

		public ClassFileManager(JavaFileManager fileManager,
				ClassReloader classReloader) {
			super(fileManager);
			this.classReloader = classReloader;
		}

		@Override
		public FileObject getFileForInput(Location location,
				String packageName, String relativeName) throws IOException {
			FileObject o = fileObjects.get(uri(location, packageName,
					relativeName));
			if (o != null)
				return o;
			return super.getFileForInput(location, packageName, relativeName);
		}

		public void putFileForInput(StandardLocation location,
				String packageName, String relativeName, JavaFileObject file) {
			fileObjects.put(uri(location, packageName, relativeName), file);
		}

		private URI uri(Location location, String packageName,
				String relativeName) {
			return URI.create(location.getName() + '/' + packageName + '/'
					+ relativeName);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location,
				String qualifiedName, Kind kind, FileObject outputFile)
				throws IOException {
			JavaFileObject file = new ComplexJavaFileObject(qualifiedName, kind);
			classReloader.add(qualifiedName, file);
			return file;
		}

		@Override
		public ClassLoader getClassLoader(Location location) {
			return classReloader;
		}

		@Override
		public String inferBinaryName(Location loc, JavaFileObject file) {
			if (file instanceof ComplexJavaFileObject)
				return file.getName();
			return super.inferBinaryName(loc, file);
		}

		@Override
		public Iterable<JavaFileObject> list(Location location,
				String packageName, Set<Kind> kinds, boolean recurse)
				throws IOException {
			Iterable<JavaFileObject> result = super.list(location, packageName,
					kinds, recurse);

			ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

			if (location == StandardLocation.CLASS_PATH
					&& kinds.contains(JavaFileObject.Kind.CLASS)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.CLASS
							&& file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}

				files.addAll(classReloader.files());
			} else if (location == StandardLocation.SOURCE_PATH
					&& kinds.contains(JavaFileObject.Kind.SOURCE)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.SOURCE
							&& file.getName().startsWith(packageName)) {
						files.add(file);
					}
				}
			}

			for (JavaFileObject file : result) {
				files.add(file);
			}

			return files;
		}
	}
}
