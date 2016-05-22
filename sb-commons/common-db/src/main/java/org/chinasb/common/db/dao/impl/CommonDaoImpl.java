package org.chinasb.common.db.dao.impl;

import java.io.Serializable;
import java.util.Collection;

import org.chinasb.common.db.dao.CommonDao;
import org.chinasb.common.db.model.BaseModel;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

/**
 * 通用DAO
 * 
 * @author zhujuan
 */
@Service
public class CommonDaoImpl implements CommonDao {

    protected HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory0(SessionFactory sessionFactory) {
        if ((hibernateTemplate == null)
                || (sessionFactory != hibernateTemplate.getSessionFactory())) {
            hibernateTemplate = new HibernateTemplate(sessionFactory);
        }
    }

	/**
	 * Return the Hibernate SessionFactory used by this DAO.
	 */
    public final SessionFactory getSessionFactory() {
        return hibernateTemplate != null ? hibernateTemplate.getSessionFactory() : null;
    }

    /**
	 * Obtain a Hibernate Session, either from the current transaction or
	 * a new one. The latter is only allowed if the
	 * {@link org.springframework.orm.hibernate3.HibernateTemplate#setAllowCreate "allowCreate"}
	 * setting of this bean's {@link #setHibernateTemplate HibernateTemplate} is "true".
	 * <p><b>Note that this is not meant to be invoked from HibernateTemplate code
	 * but rather just in plain Hibernate code.</b> Either rely on a thread-bound
	 * Session or use it in combination with {@link #releaseSession}.
	 * <p>In general, it is recommended to use HibernateTemplate, either with
	 * the provided convenience operations or with a custom HibernateCallback
	 * that provides you with a Session to work on. HibernateTemplate will care
	 * for all resource management and for proper exception conversion.
	 * @return the Hibernate Session
	 * @throws DataAccessResourceFailureException if the Session couldn't be created
	 * @throws IllegalStateException if no thread-bound Session found and allowCreate=false
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession(SessionFactory, boolean)
	 */
    protected final Session getSession() throws DataAccessResourceFailureException,
            IllegalStateException {
        return getSessionFactory().getCurrentSession();
    }

	/**
	 * 创建一个Criteria对象
	 * @param entityClazz
	 * @return
	 */
    public <T> Criteria createCriteria(Class<T> entityClazz) {
        return getSession().createCriteria(entityClazz);
    }

    @Override
    public <T> T get(Serializable id, Class<T> entityClazz) {
        return hibernateTemplate.get(entityClazz, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseModel<PK>, PK extends Comparable<PK> & Serializable> void save(T... entities) {
        for (T entity : entities) {
            hibernateTemplate.save(entity);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void update(T... entities) {
        for (T entity : entities) {
            hibernateTemplate.update(entity);
        }
    }

    @Override
    public <T> void update(Collection<T> entities) {
        for (T entity : entities) {
            hibernateTemplate.update(entity);
        }
    }

    @Override
    public <T> void delete(Serializable id, Class<T> entityClazz) {
        T entity = get(id, entityClazz);
        if (entity != null) {
            hibernateTemplate.delete(entity);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(DetachedCriteria detachedCriteria) {
        if (detachedCriteria != null) {
            Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
            return (T) criteria.list();
        }
        return null;
    }

    @Override
    public int execute(String sql) {
        if (!Strings.isNullOrEmpty(sql)) {
            SQLQuery sqlQuery = getSession().createSQLQuery(sql);
            return sqlQuery.executeUpdate();
        }
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T query(String sql) {
        if (!Strings.isNullOrEmpty(sql)) {
            SQLQuery sqlQuery = getSession().createSQLQuery(sql);
            return (T) sqlQuery.list();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T query(String sql, Class<T> entityClazz) {
        if (!Strings.isNullOrEmpty(sql)) {
            SQLQuery sqlQuery = getSession().createSQLQuery(sql);
            sqlQuery.addEntity(entityClazz);
            return (T) sqlQuery.list();
        }
        return null;
    }
}
