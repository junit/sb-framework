package org.chinasb.common.db.model;

import org.chinasb.common.lock.Entity;
import java.io.Serializable;

/**
 * 基础实体模型
 * @author zhujuan
 * @param <PK>
 */
public abstract class BaseModel<PK extends Comparable<PK> & Serializable>
        implements
            Entity<PK>,
            Serializable {
    private static final long serialVersionUID = 5106282759565182143L;

    /**
     * 获得唯一标识
     */
    public abstract PK getId();

    /**
     * 设置唯一标识
     */
    public abstract void setId(PK id);

    /**
     * 获得唯一标识
     */
    public PK getIdentity() {
        return getId();
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[" + getId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        if (!(o instanceof BaseModel)) return false;
        final BaseModel rhs = (BaseModel) o;
        return (getId() != null) && (rhs.getId() != null) && (getId().equals(rhs.getId()));
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = result * PRIME + super.hashCode();
        result = result * PRIME + (getId() == null ? 0 : getId().hashCode());
        return result;
    }
}
