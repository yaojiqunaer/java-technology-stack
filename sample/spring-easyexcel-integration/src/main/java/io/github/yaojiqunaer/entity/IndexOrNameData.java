package io.github.yaojiqunaer.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = false)
@ToString
public class IndexOrNameData {

    @ExcelProperty("姓名")
    private String doubleData;
    /**
     * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
     */
    @ExcelProperty("test")
    private String string;
    @ExcelProperty("name")
    private String name;
}