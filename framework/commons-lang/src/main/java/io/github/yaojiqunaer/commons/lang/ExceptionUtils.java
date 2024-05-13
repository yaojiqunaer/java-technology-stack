package io.github.yaojiqunaer.commons.lang;

public class ExceptionUtils {

    /**
     * 工具类不能使用构造方法初始化
     */
    public static void throwUtilConstructor() {
        throw new UnsupportedOperationException("Utility class");
    }

}
