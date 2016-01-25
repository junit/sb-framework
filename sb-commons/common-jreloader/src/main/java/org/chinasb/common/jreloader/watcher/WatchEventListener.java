package org.chinasb.common.jreloader.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface WatchEventListener {

    boolean support(Path file, WatchEvent.Kind<?> kind);

    void onWatchEvent(String dir, Path file, WatchEvent.Kind<?> kind);
}
