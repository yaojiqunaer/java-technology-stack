package io.github.yaojiqunaer.commons.lang.log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class LogMaskUtil {

    /**
     * 掩码配置类
     */
    public static class MaskConfig {
        private final Set<String> fieldPaths;
        private final int visiblePrefixLength;
        private final int visibleSuffixLength;
        private final Double visiblePrefixPercent;
        private final Double visibleSuffixPercent;
        private final char maskChar;
        final boolean maskAll;

        private MaskConfig(Builder builder) {
            this.fieldPaths = builder.fieldPaths;
            this.visiblePrefixLength = builder.visiblePrefixLength;
            this.visibleSuffixLength = builder.visibleSuffixLength;
            this.visiblePrefixPercent = builder.visiblePrefixPercent;
            this.visibleSuffixPercent = builder.visibleSuffixPercent;
            this.maskChar = builder.maskChar;
            this.maskAll = builder.maskAll;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Set<String> fieldPaths = new HashSet<>();
            private Double visiblePrefixPercent = 0.3D;
            private Double visibleSuffixPercent = 0.3D;
            private int visiblePrefixLength = 5;
            private int visibleSuffixLength = 2;
            private char maskChar = '*';
            private boolean maskAll = false;

            public Builder fieldPaths(String... paths) {
                this.fieldPaths.addAll(Arrays.asList(paths));
                return this;
            }

            public Builder visiblePrefixLength(int length) {
                this.visiblePrefixLength = length;
                return this;
            }

            public Builder visibleSuffixLength(int length) {
                this.visibleSuffixLength = length;
                return this;
            }

            public Builder visiblePrefixPercent(double percent) {
                if (percent < 0 || percent > 1) {
                    throw new IllegalArgumentException("Percentage must be between 0 and 1");
                }
                this.visiblePrefixPercent = percent;
                return this;
            }

            public Builder visibleSuffixPercent(double percent) {
                if (percent < 0 || percent > 1) {
                    throw new IllegalArgumentException("Percentage must be between 0 and 1");
                }
                this.visibleSuffixPercent = percent;
                return this;
            }

            public Builder maskChar(char maskChar) {
                this.maskChar = maskChar;
                return this;
            }

            public Builder maskAll(boolean maskAll) {
                this.maskAll = maskAll;
                return this;
            }

            public MaskConfig build() {
                return new MaskConfig(this);
            }
        }
    }

    public static String mask(Object obj) {
        return mask(obj, MaskConfig.builder().build());
    }

    /**
     * 将对象转换为格式化的字符串并进行掩码处理
     */
    public static String mask(Object obj, MaskConfig maskConfig) {
        if (obj == null) {
            return "null";
        }
        try {
            if (isSimpleType(obj)) {
                return maskValue(obj.toString(), maskConfig);
            }
            StringBuilder sb = new StringBuilder();
            appendMaskedObject(sb, obj, "", maskConfig, new HashSet<>());
            return sb.toString();
        } catch (Exception e) {
            return "Error generating masked string: " + e.getMessage();
        }
    }

    /**
     * 判断是否为简单类型
     */
    private static boolean isSimpleType(Object obj) {
        return obj instanceof String ||
                obj instanceof Number ||
                obj instanceof Boolean ||
                obj instanceof Character ||
                obj instanceof Date ||
                obj.getClass().isPrimitive();
    }

    /**
     * 递归处理对象属性
     */
    private static void appendMaskedObject(StringBuilder sb, Object obj, String currentPath,
                                           MaskConfig maskConfig, Set<Object> processed) {
        if (obj == null || processed.contains(obj)) {
            sb.append("null");
            return;
        }
        // 防止循环引用
        processed.add(obj);
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            appendMaskedArray(sb, obj, currentPath, maskConfig, processed);
            return;
        }
        if (obj instanceof Collection) {
            appendMaskedCollection(sb, (Collection<?>) obj, currentPath, maskConfig, processed);
            return;
        }
        if (obj instanceof Map) {
            appendMaskedMap(sb, (Map<?, ?>) obj, currentPath, maskConfig, processed);
            return;
        }
        if (isSimpleType(obj)) {
            sb.append(maskValue(obj.toString(), maskConfig, currentPath));
            return;
        }
        sb.append(clazz.getSimpleName()).append(" {");
        List<Field> fields = getAllFields(clazz);
        boolean firstField = true;
        for (Field field : fields) {
            if (!isAccessible(field)) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (!firstField) {
                    sb.append(", ");
                }
                String fieldPath = currentPath.isEmpty() ? field.getName() : currentPath + "." + field.getName();
                sb.append(field.getName()).append("=");
                if (value == null) {
                    sb.append("null");
                } else if (isSimpleType(value)) {
                    sb.append(maskValue(value.toString(), maskConfig, fieldPath));
                } else {
                    appendMaskedObject(sb, value, fieldPath, maskConfig, new HashSet<>(processed));
                }
                firstField = false;
            } catch (IllegalAccessException e) {
                sb.append(field.getName()).append("=AccessDenied");
            }
        }
        sb.append("}");
    }

    /**
     * 处理数组
     */
    private static void appendMaskedArray(StringBuilder sb, Object array, String currentPath,
                                          MaskConfig maskConfig, Set<Object> processed) {
        if (!array.getClass().isArray()) {
            sb.append("null");
            return;
        }
        int length = java.lang.reflect.Array.getLength(array);
        sb.append("[");
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object element = java.lang.reflect.Array.get(array, i);
            String elementPath = currentPath.isEmpty() ? "[" + i + "]" : currentPath + "[" + i + "]";
            if (element == null) {
                sb.append("null");
            } else if (isSimpleType(element)) {
                sb.append(maskValue(element.toString(), maskConfig, elementPath));
            } else {
                appendMaskedObject(sb, element, elementPath, maskConfig, new HashSet<>(processed));
            }
        }
        sb.append("]");
    }

    /**
     * 处理集合
     */
    private static void appendMaskedCollection(StringBuilder sb, Collection<?> collection,
                                               String currentPath, MaskConfig maskConfig,
                                               Set<Object> processed) {
        sb.append("[");
        int index = 0;
        for (Object element : collection) {
            if (index > 0) {
                sb.append(", ");
            }
            String elementPath = currentPath.isEmpty() ? "[" + index + "]" : currentPath + "[" + index + "]";
            if (element == null) {
                sb.append("null");
            } else if (isSimpleType(element)) {
                sb.append(maskValue(element.toString(), maskConfig, elementPath));
            } else {
                appendMaskedObject(sb, element, elementPath, maskConfig, new HashSet<>(processed));
            }
            index++;
        }
        sb.append("]");
    }

    /**
     * 处理Map
     */
    private static void appendMaskedMap(StringBuilder sb, Map<?, ?> map, String currentPath,
                                        MaskConfig maskConfig, Set<Object> processed) {
        sb.append("{");
        boolean firstEntry = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!firstEntry) {
                sb.append(", ");
            }
            String keyPath = currentPath + ".key[" + entry.getKey() + "]";
            String valuePath = currentPath + "[" + entry.getKey() + "]";
            if (entry.getKey() == null) {
                sb.append("null");
            } else if (isSimpleType(entry.getKey())) {
                sb.append(maskValue(entry.getKey().toString(), maskConfig, keyPath));
            } else {
                appendMaskedObject(sb, entry.getKey(), keyPath, maskConfig, new HashSet<>(processed));
            }
            sb.append("=");
            if (entry.getValue() == null) {
                sb.append("null");
            } else if (isSimpleType(entry.getValue())) {
                sb.append(maskValue(entry.getValue().toString(), maskConfig, valuePath));
            } else {
                appendMaskedObject(sb, entry.getValue(), valuePath, maskConfig, new HashSet<>(processed));
            }
            firstEntry = false;
        }
        sb.append("}");
    }

    /**
     * 对值进行掩码处理
     */
    private static String maskValue(String value, MaskConfig maskConfig, String fieldPath) {
        if (maskConfig.maskAll || shouldMask(fieldPath, maskConfig.fieldPaths)) {
            return maskValue(value, maskConfig);
        }
        return value;
    }

    /**
     * 判断字段路径是否应该被掩码
     */
    private static boolean shouldMask(String actualPath, Set<String> maskPaths) {
        if (maskPaths.contains(actualPath)) {
            return true;
        }
        for (String maskPath : maskPaths) {
            if (isPathMatch(actualPath, maskPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断实际路径是否匹配配置的路径模式
     */
    private static boolean isPathMatch(String actualPath, String patternPath) {
        if (actualPath.equals(patternPath)) {
            return true;
        }
        String regex = patternPath
                .replace(".", "\\.")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("*", ".*");
        if (!patternPath.contains("[")) {
            String[] parts = patternPath.split("\\.");
            StringBuilder enhancedRegex = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    enhancedRegex.append("\\.");
                }
                enhancedRegex.append(parts[i]);
                if (i < parts.length - 1) {
                    enhancedRegex.append("(\\[\\d+\\])?");
                }
            }
            regex = enhancedRegex.toString();
        }
        return actualPath.matches(regex);
    }

    /**
     * 对单个值进行掩码处理（支持百分比配置）
     * 百分比配置优先级高于固定长度配置
     */
    private static String maskValue(String value, MaskConfig maskConfig) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        int totalLength = value.length();
        // 计算前缀和后缀可见长度（百分比优先级高于固定长度）
        int prefixLength = calculateVisibleLength(totalLength, maskConfig.visiblePrefixPercent,
                maskConfig.visiblePrefixLength);
        int suffixLength = calculateVisibleLength(totalLength, maskConfig.visibleSuffixPercent,
                maskConfig.visibleSuffixLength);
        // 如果前缀+后缀长度超过总长度，强制使用末尾一半策略
        if (prefixLength + suffixLength >= totalLength) {
            return maskWithHalfSuffix(value, maskConfig.maskChar);
        }

        // 正常掩码逻辑
        StringBuilder masked = new StringBuilder();
        masked.append(value, 0, prefixLength);
        int maskLength = totalLength - prefixLength - suffixLength;
        masked.append(String.valueOf(maskConfig.maskChar).repeat(Math.max(0, maskLength)));
        if (suffixLength > 0) {
            masked.append(value.substring(totalLength - suffixLength));
        }
        return masked.toString();
    }

    /**
     * 计算可见长度（百分比优先级高于固定长度）
     */
    private static int calculateVisibleLength(int totalLength, Double percent, int fixedLength) {
        if (percent != null) {
            // 使用百分比计算长度（四舍五入）
            return Math.max(0, (int) Math.round(totalLength * percent));
        }
        // 使用固定长度，但不能超过总长度
        return Math.min(fixedLength, totalLength);
    }

    /**
     * 强制只显示末尾一半长度的掩码策略
     */
    private static String maskWithHalfSuffix(String value, char maskChar) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        int totalLength = value.length();
        // 计算应该显示的末尾长度（总长度的一半，向下取整）
        int suffixLength = totalLength / 2;
        // 如果总长度很短（<=2），则只显示最后1个字符
        if (totalLength <= 2) {
            suffixLength = totalLength == 2 ? 1 : 0;
        }
        // 如果计算出的后缀长度为0，则全部掩码
        if (suffixLength == 0) {
            return String.valueOf(maskChar).repeat(totalLength);
        }
        // 构建掩码字符串
        int maskLength = totalLength - suffixLength;
        StringBuilder masked = new StringBuilder();
        masked.append(String.valueOf(maskChar).repeat(maskLength));
        masked.append(value.substring(totalLength - suffixLength));
        return masked.toString();
    }

    /**
     * 获取类的所有字段（包括父类）
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 判断字段是否可访问
     */
    private static boolean isAccessible(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

}