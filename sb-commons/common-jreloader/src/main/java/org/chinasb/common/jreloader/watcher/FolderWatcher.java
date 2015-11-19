package org.chinasb.common.jreloader.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * folder watcher implementation.
 * 
 * @author zhujuan
 *
 */
public class FolderWatcher implements Runnable {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FolderWatcher.class);

	private String[] dirNames;
	private WatchService watchService;
	private final Map<WatchKey, Path> keys = new HashMap<WatchKey, Path>();
	private final List<WatchEventListener> listeners = new ArrayList<WatchEventListener>();

	public FolderWatcher(String[] dirNames) {
		if(dirNames == null) {
			throw new IllegalArgumentException("watchFloder is null!");
		}
		this.dirNames = dirNames;
		try {
	          watchService = FileSystems.getDefault().newWatchService();
            for (String dirName : this.dirNames) {
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
                registerDirectory(Paths.get(dirName));
            }
		} catch (IOException e) {
			throw new RuntimeException("init failed!", e);
		}
	}

	/**
	 * Register the given directory with the WatchService.
	 * @param dir
	 * @throws IOException
	 */
	private void register(Path dir) throws IOException {
		WatchKey watchKey = dir.register(watchService, new WatchEvent.Kind[] {
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY },
				SensitivityWatchEventModifier.HIGH);
        Path prev = keys.get(watchKey);
        if (LOGGER.isDebugEnabled()) {
            if (prev == null) {
                LOGGER.debug("Directory : '{}' will be monitored for changes", dir);
            }
        } else {
            if (!dir.equals(prev)) {
                LOGGER.debug("Directory updating : '{}' -> '{}'", prev, dir);
            }
        }
		keys.put(watchKey, dir);
	}

	/**
	 * register directory and sub-directories
	 * @param path
	 * @throws IOException
	 */
	public void registerDirectory(Path path) {
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					register(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOGGER.error("Failed to register the directory '{}'", path);
		}
	}

	@Override
	public void run() {
		while (true) {
			WatchKey watchKey;
			try {
				watchKey = watchService.take();
			} catch (InterruptedException e) {
				return;
			}

			Path dir = keys.get(watchKey);
			if (dir == null) {
				continue;
			}
			
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            
			for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
				WatchEvent.Kind<?> kind = watchEvent.kind();
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}
				Path name = (Path) watchEvent.context();
				Path child = dir.resolve(name);

				if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
					registerDirectory(child);
					final File[] files = child.toFile().listFiles();
					for (File file : files) {
						final String parentFolder = file.getParent();
						handleWatchEvent(parentFolder, file.toPath(), kind);
					}
				} else {
					handleWatchEvent(dir.toString()
							.replace(File.separator, "/"), child, kind);
				}
			}
			boolean reset = watchKey.reset();
			if (!reset) {
				keys.remove(watchKey);
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	public void addWatchEventListener(WatchEventListener watchEventListener) {
		synchronized (listeners) {
			listeners.add(watchEventListener);
		}
	}

	private void handleWatchEvent(String dir, Path file, WatchEvent.Kind<?> kind) {
		List<WatchEventListener> currentListeners;

		synchronized (listeners) {
			currentListeners = new ArrayList<WatchEventListener>(listeners);
		}

		for (WatchEventListener listener : currentListeners) {
			if (listener.support(file, kind)) {
				listener.onWatchEvent(dir, file, kind);
			}
		}
	}
}