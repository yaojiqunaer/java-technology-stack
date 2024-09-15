package io.github.yaojiqunaer.jpaplus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title BaseEntity
 * @Description: 基础实体类，所有实体类均可继承该类
 * <h>
 * 说明
 * </h>
 * <p>
 * 实体继承该类的主要作用是复用实体中某些属性
 * 同时能够支持AOP进行属性设置 可以配合lombok的{@link Data @Data}, {@link Accessors @Accessors}{@link EqualsAndHashCode
 * @EqualsAndHashCode}实现链式调用 lombok在配合其他组件的一些兼容性
 * 第一：链式调用的setter方法返回了对象自身而非void，可能会导致某些组件(如easyExcel-使用cglib代理判断void方法)无法set进去值
 * 第二：jdk、idea、spring、mybatis构造和识别setter、getter方法有一些区别（属性命令不规范大小写时），导致插入数据失败
 *
 * <h>
 * 开启审计功能
 * </h>
 * <p>
 * 使用 {@link EntityListeners} 开启JAP实体对象监听器 这里主要用来作审计使用
 * 实际属性字段配合以下注解 {@linkplain CreatedBy 创建人}、{@linkplain CreatedDate 创建时间}、{@linkplain LastModifiedBy 修改人}、
 * {@linkplain LastModifiedDate 修改时间}
 * 审计功能userId的复制需要业务处理，具体实现为 {@linkplain JpaAuditorAware Long UserId赋值实现}
 * 最后在 {@link JpaAutoConfiguration} 加入注解 {@link EnableJpaAuditing} 开启JPA审计功能
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity<T extends BaseEntity<T>> implements Cloneable, Serializable {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue
    protected Long id;
    /**
     * 新建人的主键
     */
    @Column(name = "create_user")
    @CreatedBy
    protected Long createUser;
    /**
     * 更新人的主键
     */
    @Column(name = "update_user")
    @LastModifiedBy
    protected Long updateUser;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    @CreatedDate
    protected Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "update_time")
    @LastModifiedDate
    protected Date updateTime;
    /**
     * 版本号
     * {@link Version Jpa乐观锁}
     */
    @Column(name = "version", columnDefinition = "int default 0")
    //@Version
    protected Integer version;

    public Long getCreateUser() {
        return createUser;
    }

    public T setCreateUser(Long createUser) {
        this.createUser = createUser;
        return (T) this;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public T setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
        return (T) this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (T) this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (T) this;
    }

    public Integer getVersion() {
        return version;
    }

    public T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }

    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }

    /**
     * 由于没有引用对象 使用的浅拷贝
     *
     * @return Object
     * @throws CloneNotSupportedException CloneNotSupportedException
     */
    @Override
    public T clone() throws CloneNotSupportedException {
        return (T) super.clone();
    }
}
