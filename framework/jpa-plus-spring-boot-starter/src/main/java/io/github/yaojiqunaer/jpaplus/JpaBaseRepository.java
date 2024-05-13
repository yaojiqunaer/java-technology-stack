package io.github.yaojiqunaer.jpaplus;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface JpaBaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {


    /**
     * save方法  {@link CrudRepository#save(Object)} 的重载
     * Saves a given entity. Use the returned instance for further operations as the save operation might have
     * changed the entity instance completely.
     * 可以自己选择是否忽略更新实体中非空的字段
     *
     * @param entity                must not be {@literal null}.
     * @param ignoreBlankAttributes 是否忽略空字段的添加/更新入库，为true时则忽略实体中null或""的数据库
     * @return the saved entity will never be {@literal null}.
     */
    <S extends T> S save(S entity, boolean ignoreBlankAttributes);

    /**
     * 批量保存 替代 {@link this#saveAll(Iterable)}
     *
     * @param entities  实体列表
     * @param batchMode 批量模式
     * @param <S>       实体
     * @return 返回信息
     */
    @NotNull
    @Transactional(rollbackFor = Exception.class)
    <S extends T> List<S> saveAll(@NotNull Iterable<S> entities, boolean batchMode);

    /**
     * 实现查询和排序 String使用GBK排序
     *
     * @param spec     can be {@literal null}.
     * @param pageSort must not be {@literal null}.
     * @return never {@literal null}.
     */
    @NotNull
    Page<T> findAll(Specification<T> spec, @NotNull PageSort pageSort);

    /**
     * 真正的批量插入，注意entities大小，建议1000以内，否则可能会比多次提交更慢
     * 注：本方式调用使用的JDBC-TEMPLATE
     * 替代了方法{@link this#saveAll(Iterable)}执行不是批量问题
     * hibernate本身不支持批量的SQL，其批量的配置实际应该是把多条SQL放到一次提交
     * 是不是一条SQL批量插入很多数据
     *
     * @param entities 实体列表
     * @param <S>      实体
     * @return 返回信息ID
     */
    @Transactional(rollbackFor = Exception.class)
    <S extends T> int[] insertBatch(@NotNull Iterable<S> entities);
}
