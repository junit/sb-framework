package org.chinasb.common.executor.script.watcher;

import java.io.File;
import java.io.FileFilter;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * 目录监控
 * 
 * @author zhujuan
 *
 */
public class FolderWatcher implements Runnable {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FolderWatcher.class);

	private boolean started;
	private String watchFloder;
	private WatchService watchService;
	private final Map<WatchKey, Path> keys = new HashMap<WatchKey, Path>();
	private final List<WatchEventListener> listeners = new ArrayList<WatchEventListener>();

	public FolderWatcher(String watchFloder) {
		this.watchFloder = watchFloder;
		try {
			File file = new File(watchFloder);
			if (file.exists()) {
				if (file.isDirectory()) {
					FileUtils.deleteDirectory(file);
				} else {
					file.delete();
				}
			}
			file.mkdirs();
			watchService = FileSystems.getDefault().newWatchService();
			registerDirectory(Paths.get(this.watchFloder));
			started = true;
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
		if (prev == null) {
			LOGGER.debug("Directory : '{}' will be monitored for changes", dir);
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
		while (started) {
			WatchKey watchKey;
			try {
				watchKey = watchService.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(watchKey);
			if (dir == null) {
				continue;
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
					final File[] javaFiles = child.toFile().listFiles(
							(FileFilter) new SuffixFileFilter(".java"));
					for (File javaFile : javaFiles) {
						final String parentFolder = javaFile.getParent();
						handleWatchEvent(parentFolder, javaFile.toPath(), kind);
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

	private void handleWatchEvent(String dir, Path file, WatchEvent.Kind kind) {
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
	
	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
}