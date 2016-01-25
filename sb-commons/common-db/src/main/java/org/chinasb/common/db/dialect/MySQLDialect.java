package org.chinasb.common.db.dialect;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.type.BinaryType;
import org.hibernate.type.StringType;

/**
 * 注册自定义类型, 增加MYSQL对Hibernate本地SQL查询结果集中返回 text,longtext,blob,longblob支持
 * 
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
