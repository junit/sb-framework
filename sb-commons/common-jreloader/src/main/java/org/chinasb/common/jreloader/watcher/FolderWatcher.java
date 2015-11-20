package org.chinasb.common.jreloader.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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
import java.util.concurrent.ThreadFactory;

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

    private final long interval;
	private WatchService watchService;
	private final Map<WatchKey, Path> keys = new HashMap<WatchKey, Path>();
	private final List<WatchEventListener> listeners = new ArrayList<WatchEventListener>();
    private Thread thread = null;
    private ThreadFactory threadFactory;
    private volatile boolean running = false;

    public FolderWatcher() {
        this(10000);
    }
    
	public FolderWatcher(long interval) {
	    this.interval = interval;
		try {
	        watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw new RuntimeException("init failed!", e);
		}
	}

    public synchronized void start() throws Exception {
        if (running) {
            throw new IllegalStateException("Monitor is already running");
        }
        running = true;
        if (threadFactory != null) {
            thread = threadFactory.newThread(this);
        } else {
            thread = new Thread(this);
        }
        thread.start();
    }
    
    public synchronized void stop() throws Exception {
        stop(interval);
    }
    
    public synchronized void stop(long stopInterval) throws Exception {
        if (running == false) {
            throw new IllegalStateException("Monitor is not running");
        }
        running = false;
        try {
            thread.join(stopInterval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Set the thread factory.
     *
     * @param threadFactory the thread factory
     */
    public synchronized void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
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
		while (running) {
			WatchKey watchKey;
			try {
				watchKey = watchService.take();
			} catch (InterruptedException e) {
				return;
			}

            try {
                Thread.sleep(interval);
            } catch (final InterruptedException ignored) {
            }
            
            if (!running) {
                break;
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