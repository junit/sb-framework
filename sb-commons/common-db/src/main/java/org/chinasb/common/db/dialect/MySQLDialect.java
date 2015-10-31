package org.chinasb.common.db.dialect;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.type.BinaryType;
import org.hibernate.type.StringType;

/**
 * 注册自定义类型
 * @author zhujuan
 *
 */
public class MySQLDialect extends MySQL5InnoDBDialect {
    public MySQLDialect() {
        registerHibernateType(Types.LONGNVARCHAR, StringType.INSTANCE.getName());
        registerHibernateType(Types.LONGVARCHAR, StringType.INSTANCE.getName());
        registerHibernateType(Types.LONGVARBINARY, BinaryType.INSTANCE.getName());
    }
}
