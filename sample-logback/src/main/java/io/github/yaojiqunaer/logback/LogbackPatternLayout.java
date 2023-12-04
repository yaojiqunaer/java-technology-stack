package io.github.yaojiqunaer.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackPatternLayout extends PatternLayout {

    /**
     * 正则替换规则
     */
    private LogbackRegexReplaces replaces;
    /**
     * 是否开启脱敏，默认关闭(false）
     */
    private Boolean sensitive;

    public LogbackPatternLayout(LogbackRegexReplaces replaces, Boolean sensitive) {
        super();
        this.replaces = replaces;
        this.sensitive = sensitive;
    }

    /**
     * 格式化日志信息
     *
     * @param event ILoggingEvent
     * @return 日志信息
     */
    @Override
    public String doLayout(ILoggingEvent event) {
        // 占位符填充
        String msg = super.doLayout(event);
        // 脱敏处理
        return this.buildSensitiveMsg(msg);
    }

    /**
     * 根据配置对日志进行脱敏
     *
     * @param msg 消息
     * @return 脱敏后的日志信息
     */
    public String buildSensitiveMsg(String msg) {
        if (sensitive == null || !sensitive) {
            // 未开启脱敏
            return msg;
        }
        if (this.replaces == null || this.replaces.getReplace() == null || this.replaces.getReplace().isEmpty()) {
            return msg;
        }
        String sensitiveMsg = msg;
        for (RegexReplacement replace : this.replaces.getReplace()) {
            // 遍历脱敏正则 & 替换敏感数据
            sensitiveMsg = replace.format(sensitiveMsg);
        }
        return sensitiveMsg;
    }
}
