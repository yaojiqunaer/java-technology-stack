package io.github.yaojiqunaer.jpaplus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@ToString
@Getter
@Accessors(chain = true)
public class PageSort {

    @JsonIgnore
    protected Long pageNo = 1L;
    @JsonIgnore
    protected Long pageSize = 10L;
    /**
     * 排序字段名称 必须是JPA建表的实体类的属性名才支持
     */
    @JsonIgnore
    protected String sort;
    /**
     * 是否升序
     */
    @JsonIgnore
    protected Boolean asc = false;


}
