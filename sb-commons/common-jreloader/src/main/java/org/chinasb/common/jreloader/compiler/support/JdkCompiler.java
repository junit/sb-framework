package org.chinasb.common.jreloader.compiler.support;

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
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * JdkCompiler.
 * 
 * @author zhujuan
 */
public class JdkCompiler extends AbstractCompiler {

    private final static Map<URI, JavaFileObject> SOURCE_FILE_OBJECTS =
            new HashMap<URI, JavaFileObject>();
    private final static Map<String, JavaFileObject> CLASS_FILE_OBJECTS =
            new HashMap<String, JavaFileObject>();

    private final JavaCompiler compiler;
    private volatile List<String> options;

    public JdkCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new NullPointerException("Couldnt find JDK compiler on path (JDK tools.jar)");
        options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
    }

    @Override
    public Class<?> doCompile(String name, String sourceCode) throws Throwable {
        int i = name.lastIndexOf('.');
        String packageName = i < 0 ? "" : name.substring(0, i);
        String className = i < 0 ? name : name.substring(i + 1);
        final DiagnosticCollector<JavaFileObject> diagnosticCollector =
                new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager =
                compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader instanceof URLClassLoader
                && (!loader.getClass().getName().equals("sun.misc.Launcher$AppClassLoader"))) {
            try {
                URLClassLoader urlClassLoader = (URLClassLoader) loader;
                List<File> files = new ArrayList<File>();
                for (URL url : urlClassLoader.getURLs()) {
                    files.add(new File(url.getFile()));
                }
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        ClassLoaderImpl classLoader =
                AccessController.doPrivileged(new PrivilegedAction<ClassLoaderImpl>() {
                    public ClassLoaderImpl run() {
                        return new ClassLoaderImpl(loader);
                    }
                });
        JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
        JavaFileManagerImpl javaFileManager = new JavaFileManagerImpl(manager, classLoader);
        SOURCE_FILE_OBJECTS.put(
                uri(StandardLocation.SOURCE_PATH, packageName, className + Kind.SOURCE.extension),
                javaFileObject);
        Boolean result =
                compiler.getTask(null, javaFileManager, diagnosticCollector, options, null,
                        Arrays.asList(new JavaFileObject[] {javaFileObject})).call();
        javaFileManager.close();
        if (result == null || !result.booleanValue()) {
            StringBuffer sb = new StringBuffer();
            for (Diagnostic<?> diagnostic : diagnosticCollector.getDiagnostics()) {
                sb.append("Source:[" + diagnostic.getSource() + "]\n");
                sb.append("Kind:[" + diagnostic.getKind() + "], ");
                sb.append("Code:[" + diagnostic.getCode() + "]\n");
                sb.append("Position:[" + diagnostic.getPosition() + "], ");
                sb.append("Start Position:[" + diagnostic.getStartPosition() + "], ");
                sb.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
                sb.append("LineNumber:[" + diagnostic.getLineNumber() + "], ");
                sb.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
                sb.append("Message:[" + diagnostic.getMessage(null) + "]\n");
            }
            throw new IllegalStateException("Compilation failed. class: " + name + "\n"
                    + sb.toString());
        }
        return classLoader.loadClass(name);
    }

    private URI uri(Location location, String packageName, String relativeName) {
        return URI.create(location.getName() + '/' + packageName + '/' + relativeName);
    }

    private final class ClassLoaderImpl extends ClassLoader {

        ClassLoaderImpl(final ClassLoader parentClassLoader) {
            super(parentClassLoader);
        }

        public synchronized Class<?> loadClass(final String qualifiedClassName)
                throws ClassNotFoundException {
            JavaFileObject file = CLASS_FILE_OBJECTS.get(qualifiedClassName);
            if (file != null) {
                byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
                return defineClass(qualifiedClassName, bytes, 0, bytes.length);
            }
            try {
                return Class.forName(qualifiedClassName);
            } catch (ClassNotFoundException nf) {
                return super.loadClass(qualifiedClassName);
            }
        }
    }

    private static final class JavaFileObjectImpl extends SimpleJavaFileObject {

        private ByteArrayOutputStream bytecode;

        private final CharSequence source;

        public JavaFileObjectImpl(final String baseName, final CharSequence source) {
            super(URI.create(baseName + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        JavaFileObjectImpl(final String name, final Kind kind) {
            super(URI.create(name), kind);
            source = null;
        }

        public JavaFileObjectImpl(URI uri, Kind kind) {
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

    private static final class JavaFileManagerImpl extends
            ForwardingJavaFileManager<JavaFileManager> {

        public JavaFileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName,
                Kind kind, FileObject outputFile) throws IOException {
            JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
            CLASS_FILE_OBJECTS.put(qualifiedName, file);
            return file;
        }

        @Override
        public String inferBinaryName(Location loc, JavaFileObject file) {
            if (file instanceof JavaFileObjectImpl)
                return file.getName();
            return super.inferBinaryName(loc, file);
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName,
                Set<Kind> kinds, boolean recurse) throws IOException {
            Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);

            ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

            if (location == StandardLocation.CLASS_PATH
                    && kinds.contains(JavaFileObject.Kind.CLASS)) {
                for (JavaFileObject file : SOURCE_FILE_OBJECTS.values()) {
                    if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
                        files.add(file);
                    }
                }

                files.addAll(Collections.unmodifiableCollection(CLASS_FILE_OBJECTS.values()));
            } else if (location == StandardLocation.SOURCE_PATH
                    && kinds.contains(JavaFileObject.Kind.SOURCE)) {
                for (JavaFileObject file : SOURCE_FILE_OBJECTS.values()) {
                    if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
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
