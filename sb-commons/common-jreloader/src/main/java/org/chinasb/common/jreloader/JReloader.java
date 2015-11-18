package org.chinasb.common.jreloader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.JavaFileObject.Kind;

import org.apache.commons.io.FileUtils;
import org.chinasb.common.jreloader.annotation.Reloadable;
import org.chinasb.common.jreloader.watcher.FolderWatcher;
import org.chinasb.common.jreloader.watcher.WatchEventListener;
import org.chinasb.common.utility.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class JReloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(JReloader.class);
    private final JComplier jComplier = new JComplier();
    private final FolderWatcher watcher;
    private final Map<Integer, Reloader> reloaderMap = new ConcurrentHashMap<Integer, Reloader>();
    
    public JReloader() {
        String[] dirNames = System.getProperty("jreloader.dirs", ".").split("\\,");
        watcher = new FolderWatcher(dirNames);
        watcher.addWatchEventListener(new WatchEventListener() {

            @Override
            public void onWatchEvent(String dir, Path file, WatchEvent.Kind<?> kind) {
                File javaFile = file.toFile();
                if (javaFile.exists()) {
                    String fileName =
                            javaFile.getName().substring(0, javaFile.getName().indexOf("."));
                    try {
                        String packageName = null;
                        StringBuffer javaSource = new StringBuffer();
                        List<String> lines = FileUtils.readLines(javaFile, "UTF-8");
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (packageName == null && line.indexOf("package") != -1) {
                                packageName =
                                        line.substring(line.indexOf(" "), line.length() - 1).trim();
                            }
                            javaSource.append(line);
                        }
                        Class<?> clazz =
                                jComplier.compile(packageName + "." + fileName,
                                        javaSource.toString());
                        Reloadable reloadable = clazz.getAnnotation(Reloadable.class);
                        if (reloadable != null) {
                            Reloader reloader = reloaderMap.get(reloadable.module());
                            if (reloader == null) {
                                LOGGER.error("没有找到功能模块，模块[{}], 脚本[{}]重载失败!", reloadable.module(), clazz.getName());
                                return;
                            }
                            reloader.reload(clazz);
                        }
                    } catch (Throwable e) {
                        FormattingTuple message = MessageFormatter.format("脚本[{}]编译失败!", file);
                        LOGGER.error(message.getMessage(), e);
                    }
                }
            }

            @Override
            public boolean support(Path file, WatchEvent.Kind<?> kind) {
                return checkEndsWith(file.toString(), Kind.SOURCE.extension, false);
            }

            private boolean checkEndsWith(String str, String end, boolean ingoreCase) {
                int endLen = end.length();
                return str.regionMatches(ingoreCase, str.length() - endLen, end, 0, endLen);
            }
        });
        NamedThreadFactory factory = new NamedThreadFactory("脚本重载线程", true);
        Thread thread = factory.newThread(watcher);
        thread.start();
    }
    
    /**
     * 添加重载处理器
     * @param moudle
     * @param reloader
     * @return
     */
    public boolean addReloader(int moudle, Reloader reloader) {
        if (reloaderMap.containsKey(moudle)) {
            return false;
        }
        return reloaderMap.putIfAbsent(moudle, reloader) == null ? true : false;
    }
    
    public static interface Reloader {
        public void reload(Class<?> clazz);
    }
    
    public static abstract class BaseReloader implements Reloader {

        public abstract void onReload(Class<?> clazz);

        @Override
        public void reload(Class<?> clazz) {
            Reloadable reloadable = clazz.getAnnotation(Reloadable.class);
            try {
                onReload(clazz);
                LOGGER.info("模块[{}], 脚本 [{}]完成重载 ...", reloadable.module(), clazz.getName());
            } catch (Exception e) {
                FormattingTuple message = MessageFormatter.format("模块[{}], 脚本[{}]重载失败!", reloadable.module(), clazz.getName());
                LOGGER.error(message.getMessage(), e);
            }
        }
    }
}
