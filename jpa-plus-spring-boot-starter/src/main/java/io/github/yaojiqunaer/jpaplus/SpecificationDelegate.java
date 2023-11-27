package io.github.yaojiqunaer.jpaplus;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationDelegate<T> implements Specification<T> {

    private Specification delegate;
    private PageSort pageSort;
    private boolean gbkSort = false;

    public SpecificationDelegate(Specification delegate) {
        this.delegate = delegate;
    }

    public SpecificationDelegate(Specification delegate, PageSort pageSort) {
        this.delegate = delegate;
        this.gbkSort = true;
        this.pageSort = pageSort;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (gbkSort) {
            // 如果是gbk排序，则一定是String类型，构造convert函数
            Expression<String> gbkSortFunction = criteriaBuilder.function("convert", String.class,
                    root.get(pageSort.getSort()));
            Order order = pageSort.getAsc() ? criteriaBuilder.asc(gbkSortFunction) :
                    criteriaBuilder.desc(gbkSortFunction);
            query.orderBy(order);
        }
        return delegate.toPredicate(root, query, criteriaBuilder);
    }
}
