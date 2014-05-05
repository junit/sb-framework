package org.chinasb.common.db.dialect;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.type.StringType;

/**
 * MySQL方言
 * @author zhujuan
 */
public class MySQLDialect extends MySQL5InnoDBDialect {
    public MySQLDialect() {
        super.registerHibernateType(Types.LONGNVARCHAR, StringType.INSTANCE.getName());
        super.registerHibernateType(Types.LONGVARCHAR, StringType.INSTANCE.getName());
        super.registerHibernateType(Types.LONGVARBINARY, StringType.INSTANCE.getName());
    }
}
