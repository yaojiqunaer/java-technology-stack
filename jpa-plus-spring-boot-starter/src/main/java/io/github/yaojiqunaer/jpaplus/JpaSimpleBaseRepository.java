package io.github.yaojiqunaer.jpaplus;

import io.github.yaojiqunaer.commons.lang.spring.SpringContextHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@NoRepositoryBean
@Slf4j
public class JpaSimpleBaseRepository<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements JpaBaseRepository<T, ID> {

    private JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;
    private boolean gbkSort = false;
    private PageSort pageSort;
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param entityManager     must not be {@literal null}.
     */
    public JpaSimpleBaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    public JpaSimpleBaseRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * save方法  {@link CrudRepository#save(Object)} 的扩展
     * Saves a given entity. Use the returned instance for further operations as the save operation might have
     * changed the entity instance completely.
     *
     * @param entity                must not be {@literal null}.
     * @param ignoreBlankAttributes 是否忽略空字段的添加/更新入库，为true时则忽略实体中null或""的数据库
     * @return the saved entity will never be {@literal null}.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S save(S entity, boolean ignoreBlankAttributes) {
        //获取ID
        ID entityId = (ID) this.entityInformation.getId(entity);
        S managedEntity;
        S mergedEntity;
        if (entityId == null) {
            save(entity);
            //entityManager.persist(entity);
            mergedEntity = entity;
        } else {
            managedEntity = (S) this.findById(entityId).orElse(null);
            if (managedEntity == null) {
                //entityManager.persist(entity);
                save(entity);
                mergedEntity = entity;
            } else {
                if (ignoreBlankAttributes) {
                    //忽略null ""字段的更新
                    BeanUtils.copyProperties(entity, managedEntity, JpaUtils.getNullProperties(entity));
                    //id存在，则修改； id不存在，则添加
                    //entityManager.merge(managedEntity);
                    save(managedEntity);
                    mergedEntity = managedEntity;
                } else {
                    save(entity);
                    mergedEntity = entity;
                }
            }
        }
        return mergedEntity;
    }

    /**
     * 批量保存 替代 {@link this#saveAll(Iterable)}
     *
     * @param entities  实体列表
     * @param batchMode 批量模式
     * @return 返回信息
     */
    @NotNull
    @Override
    public <S extends T> List<S> saveAll(@NotNull Iterable<S> entities, boolean batchMode) {
        if (batchMode) {
            // 批量增加模式
            Assert.notNull(entities, "The given Iterable of entities not be null!");
            List<S> result = new ArrayList<>();
            for (S entity : entities) {
                entityManager.persist(entity);
                result.add(entity);
            }
            return result;
        } else {
            return saveAll(entities);
        }
    }

    /**
     * 实现查询和排序 String会使用gbk排序
     *
     * @param spec     can be {@literal null}.
     * @param pageSort must not be {@literal null}.
     * @return never {@literal null}.
     */
    @NotNull
    @Override
    public Page<T> findAll(Specification<T> spec, @NotNull PageSort pageSort) {
        List<String> stringFields = JpaUtils.findStringFields(entityInformation.getJavaType());
        Long pageStart = pageSort.getPageNo() - 1;
        Long pageSize = pageSort.getPageSize();
        Pageable jpaPageable = PageRequest.of(pageStart.intValue(), pageSize.intValue(), getJpaSort(pageSort));
        if (CollectionUtils.containsAny(stringFields, pageSort.getSort())) {
            // String类型使用GBK排序 jpa function注册来自mysql方言
            if (CollectionUtils.containsAny(stringFields, pageSort.getSort())) {
                gbkSort = true;
                this.pageSort = pageSort;
                jpaPageable = PageRequest.of(pageStart.intValue(), pageSize.intValue());
            }
        }
        return super.findAll(spec, jpaPageable);
    }

    /**
     * 实现查询和排序 String会自动使用gbk排序
     *
     * @param spec     can be {@literal null}.
     * @param pageable must not be {@literal null}.
     * @return never {@literal null}.
     */
    @NotNull
    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            List<String> stringFields = JpaUtils.findStringFields(entityInformation.getJavaType());
            if (sort.stream().count() == 1) {
                Sort.Order order = sort.get().toList().get(0);
                String property = order.getProperty();
                // String类型使用GBK排序 jpa function注册来自mysql方言
                if (CollectionUtils.containsAny(stringFields, property)) {
                    gbkSort = true;
                    pageSort = new PageSort().setPageNo((long) pageable.getPageNumber() + 1)
                            .setPageSize((long) pageable.getPageSize())
                            .setSort(order.getProperty()).setAsc(order.getDirection().isAscending());
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
                }
            }
        }
        return super.findAll(spec, pageable);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    private Sort getJpaSort(PageSort pageSort) {
        Sort sortDo = Sort.by("createTime").descending();
        if (StringUtils.isNotBlank(pageSort.getSort())) {
            sortDo = JpaSort.unsafe(pageSort.getAsc() ? ASC : DESC, pageSort.getSort());
        }
        return sortDo;
    }


    /**
     * 真正的批量插入，注意entities大小，建议1000以内，否则可能会比多次提交更慢
     * 注：本方式调用使用的JDBC-TEMPLATE
     * 替代了方法{@link this#saveAll(Iterable)}执行不是批量问题
     * hibernate本身不支持批量的SQL，其批量的配置实际应该是把多条SQL放到一次提交
     * 是不是一条SQL批量插入很多数据
     *
     * @param entities 实体列表
     * @return 返回信息ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> int[] insertBatch(@NotNull Iterable<S> entities) {
        // 获取到泛型实体的类对象
        Class<T> domainClass = super.getDomainClass();
        // 构造JPA/JDBC SQL format
        String insertSqlFormat = JpaUtils.createInsertSqlFormat(domainClass);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(IterableUtils.toList(entities).toArray());
        if (namedParameterJdbcTemplate == null) {
            // JPA的repository是JDK动态代理注入，这里只能通过spring容器上下文获取到指定bean
            namedParameterJdbcTemplate = SpringContextHolder.getBean(NamedParameterJdbcTemplate.class);
        }
        // 执行SQL
        return this.namedParameterJdbcTemplate.batchUpdate(insertSqlFormat, batch);
    }

    @NotNull
    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Sort sort) {
        SpecificationDelegate<S> objectSpecificationDelegate = gbkSort ? new SpecificationDelegate<>(spec, pageSort) :
                new SpecificationDelegate<>(spec);
        return super.getQuery(spec == null ? null : objectSpecificationDelegate, domainClass, sort);
    }

    @NotNull
    @Override
    protected <S extends T> TypedQuery<Long> getCountQuery(Specification<S> spec, Class<S> domainClass) {
        SpecificationDelegate<S> objectSpecificationDelegate = gbkSort ? new SpecificationDelegate<>(spec, pageSort) :
                new SpecificationDelegate<>(spec);
        return super.getCountQuery(spec == null ? null : objectSpecificationDelegate, domainClass);
    }
}
