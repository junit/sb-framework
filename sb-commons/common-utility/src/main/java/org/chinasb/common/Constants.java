package org.chinasb.common;

import java.util.regex.Pattern;


/**
 * Constants
 * 
 * @author zhujuan
 */
public class Constants {
    // Time
    public static final int ONE_SECOND_MILLISECOND = 1000;
    public static final int ONE_MINUTE_MILLISECOND = ONE_SECOND_MILLISECOND * 60;
    public static final int ONE_HOUR_MILLISECOND = ONE_MINUTE_MILLISECOND * 60;
    public static final int ONE_DAY_MILLISECOND = ONE_HOUR_MILLISECOND * 24;
    public static final int ONE_MINUTE_SECOND = 60;
    public static final long ONE_HOUR_SECOND = ONE_MINUTE_SECOND * 60;
    public static final long ONE_DAY_SECOND = ONE_HOUR_SECOND * 24;
    
    public static final String BACKUP_KEY = "backup";
    public static final String THREAD_NAME_KEY = "threadname";
    public static final String CORE_THREADS_KEY = "corethreads";
    public static final String THREADS_KEY = "threads";
    public static final String QUEUES_KEY = "queues";
    public static final String ALIVE_KEY = "alive";
    public static final String GROUP_KEY = "group";
    public static final String VERSION_KEY = "version";
    public static final String INTERFACE_KEY = "interface";
    public static final String GENERIC_KEY = "generic";
    public static final String ANYHOST_KEY = "anyhost";
    public static final String ANYHOST_VALUE = "0.0.0.0";
    public static final String LOCALHOST_KEY = "localhost";
    public static final String LOCALHOST_VALUE = "127.0.0.1";
    public static final String DEFAULT_KEY_PREFIX = "default.";
    public static final String DEFAULT_THREAD_NAME = "ybb";
    public static final int DEFAULT_CORE_THREADS = 0;
    public static final int DEFAULT_THREADS = 200;
    public static final int DEFAULT_QUEUES = 0;
    public static final int DEFAULT_ALIVE = 60 * 1000;

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
}
