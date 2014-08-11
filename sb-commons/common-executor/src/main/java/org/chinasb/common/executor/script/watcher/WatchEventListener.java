package org.chinasb.common.executor.script.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface WatchEventListener {

    public boolean support(Path file, WatchEvent.Kind kind);
    
    public void onWatchEvent(String dir, Path file, WatchEvent.Kind kind);
}
