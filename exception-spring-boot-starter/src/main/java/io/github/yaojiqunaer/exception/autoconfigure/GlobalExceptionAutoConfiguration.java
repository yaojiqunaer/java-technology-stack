package io.github.yaojiqunaer.exception.autoconfigure;

import io.github.yaojiqunaer.exception.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@ConditionalOnMissingBean(GlobalExceptionHandler.class)
@ImportAutoConfiguration(GlobalExceptionHandler.class)
public class GlobalExceptionAutoConfiguration {

}
