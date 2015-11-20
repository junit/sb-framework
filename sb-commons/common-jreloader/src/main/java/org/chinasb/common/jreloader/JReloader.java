package org.chinasb.common.jreloader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.tools.JavaFileObject.Kind;

import org.apache.commons.io.FileUtils;
import org.chinasb.common.jreloader.compiler.Compiler;
import org.chinasb.common.jreloader.compiler.support.JdkCompiler;
import org.chinasb.common.jreloader.watcher.FolderWatcher;
import org.chinasb.common.jreloader.watcher.WatchEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

/**
 * JReloader.
 * 
 * @author zhujuan
 *
 */
@Component
public class JReloader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JReloader.class);

	private final Compiler complier;
	private final ConcurrentMap<Integer, Reloader> reloaders;
    private final FolderWatcher watcher;

    public JReloader() throws Exception {
        complier = new JdkCompiler();
        reloaders = new ConcurrentHashMap<Integer, Reloader>();
        watcher = new FolderWatcher(Integer.getInteger("jreloader.interval", 5000));
        watcher.start();
        String[] dirNames = System.getProperty("jreloader.dirs", ".").split("\\,");
        for (String dirName : dirNames) {
            File folder = new File(dirName);
            if (!folder.exists()) {
                LOGGER.warn("directory[{}] does not exist!", folder);
                try {
                    folder.mkdirs();
                    LOGGER.info("create directory[{}] successed!", folder);
                } catch (Exception e) {
                    LOGGER.error("create directory[{}] failed!", folder);
                }
            }
            watcher.registerDirectory(Paths.get(dirName));
        }
        watcher.addWatchEventListener(new WatchEventListener() {

            @Override
            public void onWatchEvent(String dir, Path file, WatchEvent.Kind<?> kind) {
                File javaFile = file.toFile();
                if (javaFile.exists()) {
                    reload(javaFile);
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
        LOGGER.info("重载脚本功能启动成功，开始监控目录[{}]...", Arrays.toString(dirNames));
    }
	
	private void reload(File file) {
		try {
			Class<?> clazz = complier.compile(FileUtils.readFileToString(file));
			Reloadable reloadable = clazz.getAnnotation(Reloadable.class);
			if (reloadable != null) {
				Reloader reloader = reloaders.get(reloadable.module());
				if (reloader == null) {
					LOGGER.error("没有找到功能模块[{}], 脚本[{}]重载失败!",
							reloadable.module(), clazz.getName());
					return;
				}
				reloader.reload(clazz);
			}
		} catch (Throwable e) {
			FormattingTuple message = MessageFormatter.format("脚本[{}]编译失败!",
					file);
			LOGGER.error(message.getMessage(), e);
		}
	}
	
    /**
     * 添加重载处理器
     * @param moudle
     * @param reloader
     * @return
     */
	public boolean addReloader(int moudle, Reloader reloader) {
		if (reloaders.containsKey(moudle)) {
			return false;
		}
		return reloaders.putIfAbsent(moudle, reloader) == null ? true : false;
	}

	public static interface Reloader {
		public void reload(Class<?> clazz);
	}

	public static abstract class BaseReloader implements Reloader {

		public abstract void onReload(Class<?> clazz) throws Throwable;

		public abstract int getMoudle();

		@Override
		public void reload(Class<?> clazz) {
			try {
				onReload(clazz);
				LOGGER.info("模块[{}], 脚本 [{}]完成重载 ...", getMoudle(),
						clazz.getName());
			} catch (Throwable e) {
				FormattingTuple message = MessageFormatter.format(
						"模块[{}], 脚本[{}]重载失败!", getMoudle(), clazz.getName());
				LOGGER.error(message.getMessage(), e);
			}
		}
	}
}
