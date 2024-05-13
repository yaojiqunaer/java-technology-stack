package io.github.yaojiqunaer.jpaplus;

import com.google.common.collect.Lists;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JpaUtils {
    /**
     * 查询出entity值 -> JpaUtil.copyNotNullProperties(input, entity); -> save(entity)
     *
     * @param input    输入实体类
     * @param dbEntity 数据库查询出的实体类
     */
    public static void copyNotNullProperties(Object input, Object dbEntity) {
        BeanUtils.copyProperties(input, dbEntity, getNullPropertyNames(input));
    }

    /**
     * 忽略的字段：值为null、浮点数为0.0、值为空字符串""
     * <p>
     * ignoreProperties {@link BeanUtils#copyProperties(Object, Object, String...)}
     *
     * @param object 传入全部字段
     * @return 忽略的字段
     */
    private static String[] getNullPropertyNames(Object object) {
        final BeanWrapperImpl wrapper = new BeanWrapperImpl(object);
        return Stream.of(wrapper.getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .filter(propertyName -> wrapper.getPropertyValue(propertyName) == null
                        || "0.0".equals(String.valueOf(wrapper.getPropertyValue(propertyName)))
                        || "".equals(String.valueOf(wrapper.getPropertyValue(propertyName))))
                .toArray(String[]::new);
    }

    public static String[] getNullProperties(Object src) {
        //1.获取Bean
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        //2.获取Bean的属性描述
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        //3.获取Bean的空属性
        Set<String> properties = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : pds) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = srcBean.getPropertyValue(propertyName);
            if (ObjectUtils.isEmpty(propertyValue)) {
                srcBean.setPropertyValue(propertyName, null);
                properties.add(propertyName);
            }
        }
        return properties.toArray(new String[0]);
    }

    /**
     * 生成JPA Insert的语法模板
     *
     * @param entityClass JPA实体类
     * @return insert的SQL模板 如 insert into sys_user(id, name) values (:id, :name)
     */
    public static String createInsertSqlFormat(Class<?> entityClass) {
        // 获取JPA表的注解
        Table table = entityClass.getAnnotation(Table.class);
        return Optional.ofNullable(table).map(t -> {
            // 表名取注解的表名 否则取实体类名
            final String tableName = Optional.of(t.name()).orElse(entityClass.getSimpleName());
            // 所有的属性 包括父类
            List<Field> classFields = getAllFields(entityClass).stream().filter(field -> {
                // 不持久化的字段 不参与SQL组装
                return field.getAnnotation(Transient.class) == null;
            }).toList();
            // 获取SQL属性字段
            String sqlFields = classFields.stream().map(field -> {
                Column column = field.getAnnotation(Column.class);
                // SQL字段名优先取注解的值 否则取属性名
                return column == null ? field.getName() : column.name();
            }).filter(Objects::nonNull).collect(Collectors.joining(","));
            // 获取实体属性字段
            String entityFields = classFields.stream().map(Field::getName)
                    .map(f -> ":" + f).collect(Collectors.joining(","));
            return String.format("INSERT INTO %s(%s) VALUES (%s)", tableName, sqlFields, entityFields);
        }).orElse(null);
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Lists.newArrayList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static List<String> findFields(Class<?> clazz, Class<?> filedType) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> fields1 =
                Arrays.stream(fields).filter(field -> field.getType().equals(filedType)).toList();
        return fields1.stream().map(Field::getName).collect(Collectors.toList());
    }

    public static List<String> findStringFields(Class<?> clazz) {
        return findFields(clazz, String.class);
    }

}
