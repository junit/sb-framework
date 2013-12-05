package org.chinasb.common.utility;

/**
 * 时间常量
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public interface TimeConstant {
    public static final long ONE_SECOND_MILLISECOND = 1000L;
    public static final long ONE_MINUTE_MILLISECOND = 60 * ONE_SECOND_MILLISECOND;
    public static final long ONE_HOUR_MILLISECOND = 60 * ONE_MINUTE_MILLISECOND;
    public static final long ONE_DAY_MILLISECOND = 24 * ONE_HOUR_MILLISECOND;
    
    public static final int ONE_SECOND = 1;
    public static final int ONE_MINUTE_SECOND = 60 * ONE_SECOND;
    public static final int ONE_HOUR_SECOND = 60 * ONE_MINUTE_SECOND;
    public static final int ONE_DAY_SECOND = 24 * ONE_HOUR_SECOND;
}
