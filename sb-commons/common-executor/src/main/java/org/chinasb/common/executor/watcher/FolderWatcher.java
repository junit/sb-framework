package org.chinasb.common.executor.watcher;

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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 目录监控
 * 
 * @author zhujuan
 *
 */
public class FolderWatcher implements Runnable {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FolderWatcher.class);

	private String watchFloder;
	private WatchKey watchKey;
	private WatchService watchService;
	private final Map<WatchKey, Path> map = new HashMap<WatchKey, Path>();
	private final List<WatchEventListener> listeners = new ArrayList<WatchEventListener>();

	public FolderWatcher(String watchFloder) {
		this.watchFloder = watchFloder;
	}

	public void registerPath(Path path) throws IOException {
		WatchKey watchKey = path.register(watchService,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);
		map.put(watchKey, path);
	}

	public void registerTree(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				registerPath(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	public void run() {
		try {
			File file = new File(watchFloder);
			if (file.exists()) {
				FileUtils.deleteDirectory(file);
			}
			file.mkdir();
			watchService = FileSystems.getDefault().newWatchService();
			registerPath(Paths.get(watchFloder));
		} catch (IOException e) {
			throw new RuntimeException("init failed!", e);
		}

		while (true) {
			try {
				watchKey = watchService.take();
				for (WatchEvent watchEvent : watchKey.pollEvents()) {
					Path context = (Path) watchEvent.context();
					WatchEvent.Kind<?> kind = watchEvent.kind();
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
						Path subPath = map.get(watchKey);
						Path childDiectory = subPath.resolve(context);
						if (Files.isDirectory(childDiectory, LinkOption.NOFOLLOW_LINKS)) {
							try {
								registerTree(childDiectory);
							} catch (IOException e) {
								LOGGER.error("registerTree Error:", e);
							}
						}
					}
					handleWatchEvent(watchKey, watchEvent);
				}
				boolean reset = watchKey.reset();
				if (!reset) {
					map.remove(watchKey);
				}
			} catch (InterruptedException e) {
				LOGGER.error("WatchEvent Error:", e);
			}
		}
	}

	public void addWatchEventListener(WatchEventListener watchEventListener) {
		synchronized (listeners) {
			listeners.add(watchEventListener);
		}
	}

	private void handleWatchEvent(WatchKey watchKey, WatchEvent event) {
		List<WatchEventListener> currentListeners;

		synchronized (listeners) {
			currentListeners = new ArrayList<WatchEventListener>(listeners);
		}

		for (WatchEventListener listener : currentListeners) {
			listener.onWatchEvent(watchKey, event);
		}
	}

}