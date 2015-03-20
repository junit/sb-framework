package org.chinasb.common.socket.config;

import java.util.regex.Pattern;

import org.chinasb.common.utility.StringUtility;
import org.chinasb.common.utility.configuration.ConfigurableProcessor;
import org.chinasb.common.utility.configuration.Property;

/**
 * 服务器配置
 * @author zhujuan
 *
 */
public class ServerConfig {
    /**
     * 管理后台IP
     */
    @Property(defaultValue = "127.0.0.1", fileName = "server", key = "server.mis.ip")
    private static String misIp;
    /**
     * 默认端口
     */
    @Property(defaultValue = "9999", fileName = "server", key = "server.socket.port")
    private static int socketPort;
    /**
     * 读缓冲大小
     */
    @Property(defaultValue = "2048", fileName = "server", key = "server.socket.buffer.read")
    private static int readBufferSize;
    /**
     * 接收缓冲大小
     */
    @Property(defaultValue = "40960", fileName = "server", key = "server.socket.buffer.receive")
    private static int receiveBufferSize;
    /**
     * 写缓冲大小
     */
    @Property(defaultValue = "4096", fileName = "server", key = "server.socket.buffer.write")
    private static int writeBufferSize;
    /**
     * 是否启用nagle算法，TcpNoDelay=false为启用nagle算法，Nagle算法的立意是良好的,避免网络中充塞小封包,提高网络的利用率
     */
    @Property(defaultValue = "false", fileName = "server", key = "server.tcp.nodelay")
    private static boolean tcpNodelay;
    /**
     * 默认值为读取自系统的/proc/sys/net/core/somaxconn
     */
    @Property(defaultValue = "5000", fileName = "server", key = "server.max.backlog")
    private static int serverMaxBacklog;

    public static int getSocketPort() {
        return socketPort;
    }

    public static int getReadBufferSize() {
        return readBufferSize;
    }

    public static int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public static int getWriteBufferSize() {
        return writeBufferSize;
    }

    public static boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public static int getServerMaxBacklog() {
        return serverMaxBacklog;
    }

    private static Pattern[] misRegex = null;

    private static void initPattern() {
        String[] s = misIp.split(",");
        if ((s == null) || (s.length == 0)) {
            return;
        }
        misRegex = new Pattern[s.length];
        for (int i = 0; i < s.length; i++) {
            String str = s[i].trim().replace(".", "[.]").replace("*", "[0-9]*");
            misRegex[i] = Pattern.compile(str);
        }
    }

    public static boolean isAllowMisIp(String ip) {
        if (misRegex == null) {
            return false;
        }
        if (StringUtility.isBlank(ip)) {
            return false;
        }
        for (Pattern pattern : misRegex) {
            if (pattern != null) {
                if (pattern.matcher(ip).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    static {
        ConfigurableProcessor.process(ServerConfig.class);
        initPattern();
    }
}
