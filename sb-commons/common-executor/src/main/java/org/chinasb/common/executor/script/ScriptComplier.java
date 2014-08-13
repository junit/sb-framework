package org.chinasb.common.executor.script;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 脚本编译
 * @author zhujuan
 *
 */
public class ScriptComplier {
	private final static Logger LOGGER = LoggerFactory.getLogger(ScriptComplier.class);

	private URLClassLoader parentClassLoader;
	private String classpath;
	private String outputpath;
    
	
	public ScriptComplier(String outputpath) {
		if (outputpath == null) {
			throw new IllegalArgumentException("outputpath is null!");
		}
		this.outputpath = outputpath;
		this.parentClassLoader = (URLClassLoader) this.getClass()
				.getClassLoader();
		this.buildClassPath();
	}

	private void buildClassPath() {
		this.classpath = null;
		StringBuilder sb = new StringBuilder();
		for (URL url : this.parentClassLoader.getURLs()) {
			String p = url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		this.classpath = sb.append(new File(outputpath).toURI().getPath())
				.append(File.pathSeparator).toString();
	}
	
	/**
	 * 编译Java代码
	 * @param name 包名.类名(无需class后缀)
	 * @param code 代码
	 * @return
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public boolean compile(String name, String code) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				diagnostics, null, null);

		JavaFileObject jfile = new JavaSourceFromString(name, code);
		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(jfile);
		List<String> options = new ArrayList<String>();

		options.add("-encoding");
		options.add("UTF-8");
		options.add("-classpath");
		options.add(this.classpath);
		options.add("-d");
		options.add(outputpath);

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
				diagnostics, options, null, jfiles);

		boolean success = task.call();

		fileManager.close();

		if (!success) {
			String error = "";
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				error = error + compilePrint(diagnostic);
			}
			LOGGER.error(error);
		}
		return success;
	}
	
	/**
	 * 编译信息打印
	 * @param diagnostic
	 * @return
	 */
	private String compilePrint(Diagnostic<?> diagnostic) {
		StringBuffer res = new StringBuffer();
		res.append("Code:[" + diagnostic.getCode() + "]\n");
		res.append("Kind:[" + diagnostic.getKind() + "]\n");
		res.append("Position:[" + diagnostic.getPosition() + "]\n");
		res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
		res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
		res.append("Source:[" + diagnostic.getSource() + "]\n");
		res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
		res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
		res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
		return res.toString();
	}

	private class JavaSourceFromString extends SimpleJavaFileObject {

		private String code;
		
		public JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}
		
		 @Override
         public CharSequence getCharContent(boolean ignoreEncodingErrors) {
             return code;
         }
	}
}
