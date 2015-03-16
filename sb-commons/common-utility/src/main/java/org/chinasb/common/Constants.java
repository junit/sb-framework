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
    

    public static final String  ENABLED_KEY                        = "enabled";

    public static final String  DISABLED_KEY                       = "disabled";

    public static final String  VALIDATION_KEY                     = "validation";

    public static final String  CACHE_KEY                          = "cache";

    public static final String  DYNAMIC_KEY                        = "dynamic";

    public static final String  SENT_KEY                           = "sent";

    public static final boolean DEFAULT_SENT                       = false;

    public static final int     DEFAULT_IO_THREADS                 = Runtime.getRuntime()
                                                                           .availableProcessors() + 1;
    
    public static final String  DEFAULT_PROXY                      = "javassist";

    public static final int     DEFAULT_PAYLOAD                    = 8 * 1024 * 1024;                      // 8M

    public static final String  DEFAULT_CHARSET                    = "UTF-8";

    public static final int     DEFAULT_WEIGHT                     = 100;

    public static final int     DEFAULT_FORKS                      = 2;

    public static final String  DEFAULT_THREAD_NAME                = "worker";

    public static final int     DEFAULT_CORE_THREADS               = 0;

    public static final int     DEFAULT_THREADS                    = 200;

    public static final int     DEFAULT_QUEUES                     = 0;

    public static final int     DEFAULT_ALIVE                      = 60 * 1000;

    public static final int     DEFAULT_CONNECTIONS                = 0;

    public static final int     DEFAULT_ACCEPTS                    = 0;

    public static final int     DEFAULT_IDLE_TIMEOUT               = 600 * 1000;

    public static final int     DEFAULT_HEARTBEAT                  = 60 * 1000;

    public static final int     DEFAULT_TIMEOUT                    = 1000;

    public static final int     DEFAULT_CONNECT_TIMEOUT            = 3000;

    public static final int     DEFAULT_RETRIES                    = 2;

    // default buffer size is 8k.
    public static final int     DEFAULT_BUFFER_SIZE                = 8 * 1024;

    public static final int     MAX_BUFFER_SIZE                    = 16 * 1024;

    public static final int     MIN_BUFFER_SIZE                    = 1 * 1024;

    public static final String  REMOVE_VALUE_PREFIX                = "-";

    public static final String  HIDE_KEY_PREFIX                    = ".";

    public static final String  DEFAULT_KEY_PREFIX                 = "default.";

    public static final String  DEFAULT_KEY                        = "default";

    public static final String  LOADBALANCE_KEY                    = "loadbalance";

    public static final String  ANYHOST_KEY                        = "anyhost";

    public static final String  ANYHOST_VALUE                      = "0.0.0.0";

    public static final String  LOCALHOST_KEY                      = "localhost";

    public static final String  LOCALHOST_VALUE                    = "127.0.0.1";

    public static final String  APPLICATION_KEY                    = "application";

    public static final String  LOCAL_KEY                          = "local";

    public static final String  PROTOCOL_KEY                       = "protocol";

    public static final String  PROXY_KEY                          = "proxy";

    public static final String  WEIGHT_KEY                         = "weight";

    public static final String  FORKS_KEY                          = "forks";

    public static final String  DEFAULT_THREADPOOL                 = "limited";

    public static final String  DEFAULT_CLIENT_THREADPOOL          = "cached";

    public static final String  THREADPOOL_KEY                     = "threadpool";

    public static final String  THREAD_NAME_KEY                    = "threadname";

    public static final String  IO_THREADS_KEY                     = "iothreads";

    public static final String  CORE_THREADS_KEY                   = "corethreads";

    public static final String  THREADS_KEY                        = "threads";

    public static final String  QUEUES_KEY                         = "queues";

    public static final String  ALIVE_KEY                          = "alive";

    public static final String  EXECUTES_KEY                       = "executes";

    public static final String  BUFFER_KEY                         = "buffer";

    public static final String  PAYLOAD_KEY                        = "payload";

    public static final String  ACCESS_LOG_KEY                     = "accesslog";

    public static final String  ACTIVES_KEY                        = "actives";

    public static final String  CONNECTIONS_KEY                    = "connections";

    public static final String  ACCEPTS_KEY                        = "accepts";

    public static final String  IDLE_TIMEOUT_KEY                   = "idle.timeout";

    public static final String  HEARTBEAT_KEY                      = "heartbeat";

    public static final String  HEARTBEAT_TIMEOUT_KEY              = "heartbeat.timeout";

    public static final String  CONNECT_TIMEOUT_KEY                = "connect.timeout";

    public static final String  TIMEOUT_KEY                        = "timeout";

    public static final String  RETRIES_KEY                        = "retries";

    public static final String  SERVER_KEY                         = "server";

    public static final String  CLIENT_KEY                         = "client";

    public static final String  ID_KEY                             = "id";

    public static final String  ASYNC_KEY                          = "async";

    public static final String  RETURN_KEY                         = "return";

    public static final String  TOKEN_KEY                          = "token";

    public static final String  CHARSET_KEY                        = "charset";

    public static final String  RECONNECT_KEY                      = "reconnect";

    public static final String  SEND_RECONNECT_KEY                 = "send.reconnect";

    public static final int     DEFAULT_RECONNECT_PERIOD           = 2000;

    public static final String  SHUTDOWN_TIMEOUT_KEY               = "shutdown.timeout";

    public static final int     DEFAULT_SHUTDOWN_TIMEOUT           = 1000 * 60 * 15;

    public static final String  PID_KEY                            = "pid";

    public static final String  TIMESTAMP_KEY                      = "timestamp";
    
    public static final String  WARMUP_KEY                         = "warmup";

    public static final int     DEFAULT_WARMUP                     = 10 * 60 * 1000;

    public static final String  CHECK_KEY                          = "check";

    public static final String  REGISTER_KEY                       = "register";

    public static final String  SUBSCRIBE_KEY                      = "subscribe";

    public static final String  GROUP_KEY                          = "group";

    public static final String  PATH_KEY                           = "path";

    public static final String  FILE_KEY                           = "file";

    public static final String  WAIT_KEY                           = "wait";

    public static final String  VERSION_KEY                        = "version";

    public static final String  DISPATCHER_KEY                     = "dispatcher";

    public static final String  CHANNEL_HANDLER_KEY                = "channel.handler";

    public static final String  DEFAULT_CHANNEL_HANDLER            = "default";

    public static final String  ANY_VALUE                          = "*";

    public static final String  COMMA_SEPARATOR                    = ",";

    public static final Pattern COMMA_SPLIT_PATTERN                = Pattern
                                                                           .compile("\\s*[,]+\\s*");

    public final static String  PATH_SEPARATOR                     = "/";

    public static final String  REGISTRY_SEPARATOR                 = "|";

    public static final Pattern REGISTRY_SPLIT_PATTERN             = Pattern
                                                                           .compile("\\s*[|;]+\\s*");

    public static final String  SEMICOLON_SEPARATOR                = ";";

    public static final Pattern SEMICOLON_SPLIT_PATTERN            = Pattern
                                                                           .compile("\\s*[;]+\\s*");

    public static final String  CONNECT_QUEUE_CAPACITY             = "connect.queue.capacity";

    public static final String  CONNECT_QUEUE_WARNING_SIZE         = "connect.queue.warning.size";

    public static final int     DEFAULT_CONNECT_QUEUE_WARNING_SIZE = 1000;

    public static final String  CHANNEL_ATTRIBUTE_READONLY_KEY     = "channel.readonly";

    public static final String  CHANNEL_READONLYEVENT_SENT_KEY     = "channel.readonly.sent";

    public static final String  CHANNEL_SEND_READONLYEVENT_KEY     = "channel.readonly.send";

    /*
     * private Constants(){ }
     */
}
