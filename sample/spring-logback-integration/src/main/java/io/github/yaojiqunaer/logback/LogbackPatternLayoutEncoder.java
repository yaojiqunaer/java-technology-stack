package io.github.yaojiqunaer.logback;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * @Title LogbackPatternLayoutEncoder
 * @Description: Logback正则替换编码工具
 * <p>
 * 请参考<a href="https://blog.csdn.net/blue_driver/article/details/125007794"></a>
 * </p>
 */
public class LogbackPatternLayoutEncoder extends PatternLayoutEncoder {
    /**
     * 正则替换规则
     */
    private LogbackRegexReplaces replaces;
    /**
     * 是否开启脱敏，默认关闭(false）
     */
    private Boolean sensitive = false;

    /**
     * 使用自定义TbspLogbackPatternLayout格式化输出
     */
    @Override
    public void start() {
        LogbackPatternLayout patternLayout = new LogbackPatternLayout(replaces, sensitive);
        patternLayout.setContext(context);
        patternLayout.setPattern(this.getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        started = true;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public LogbackRegexReplaces getReplaces() {
        return replaces;
    }

    public void setReplaces(LogbackRegexReplaces replaces) {
        this.replaces = replaces;
    }


}
