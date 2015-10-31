package org.chinasb.common.db.model;

import java.io.Serializable;

import org.chinasb.common.lock.IEntity;

/**
 * 实体抽象基类
 * @author zhujuan
 *
 * @param <PK>
 */
@SuppressWarnings("serial")
public abstract class BaseModel<PK extends Comparable<PK> & Serializable> implements IEntity<PK>,
        Serializable {
    
    /**
     * 获取实体ID
     */
    public abstract PK getId();

    /**
     * 设置实体ID
     */
    public abstract void setId(PK id);

    @Override
    public String toString() {
        return getClass().getName() + "[" + getId() + "]";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseModel)) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        BaseModel rhs = (BaseModel) o;
        return (getId() != null) && (rhs.getId() != null) && (getId().equals(rhs.getId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        return result;
    }
    
    /**
     * 获取实体标识
     */
    public PK getIdentity() {
        return getId();
    }
}
