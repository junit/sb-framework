package org.chinasb.common.socket.config;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigCache;
import org.aeonbits.owner.Converter;

import com.google.common.base.Strings;

/**
 * 服务器配置
 * 
 * @author zhujuan
 *
 */
@Sources({"classpath:server.properties"})
public interface ServerConfig extends Config {
    /**
     * 管理后台IP
     */
    @Key("server.mis.ip")
    @DefaultValue("127.0.0.1")
    @ConverterClass(MisRegexConverter.class)
    Pattern[] misRegex();

    /**
     * 公共队列大小
     */
    @Key("common.queue.size")
    @DefaultValue("5")
    int commonQueueSize();

    /**
     * 默认端口
     */
    @Key("server.socket.port")
    @DefaultValue("9999")
    int socketPort();

    /**
     * 读缓冲大小
     */
    @Key("server.socket.buffer.read")
    @DefaultValue("2048")
    int readBufferSize();

    /**
     * 接收缓冲大小
     */
    @Key("server.socket.buffer.receive")
    @DefaultValue("40960")
    int receiveBufferSize();

    /**
     * 写缓冲大小
     */
    @Key("server.socket.buffer.write")
    @DefaultValue("4096")
    int writeBufferSize();

    /**
     * 是否启用nagle算法，TcpNoDelay=false为启用nagle算法，Nagle算法的立意是良好的,避免网络中充塞小封包,提高网络的利用率
     */
    @Key("server.tcp.nodelay")
    @DefaultValue("false")
    boolean tcpNodelay();

    /**
     * 默认值为读取自系统的/proc/sys/net/core/somaxconn
     */
    @Key("server.max.backlog")
    @DefaultValue("1024")
    int serverMaxBacklog();

    class MisRegexConverter implements Converter<Pattern> {
        public Pattern convert(Method targetMethod, String text) {
            String str = text.trim().replace(".", "[.]").replace("*", "[0-9]*");
            return Pattern.compile(str);
        }
    }

    public static boolean isAllowMisIp(String ip) {
        ServerConfig sc = ConfigCache.getOrCreate(ServerConfig.class);
        Pattern[] misRegex = sc.misRegex();
        if (misRegex == null) {
            return false;
        }
        if (Strings.isNullOrEmpty(ip)) {
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
}
