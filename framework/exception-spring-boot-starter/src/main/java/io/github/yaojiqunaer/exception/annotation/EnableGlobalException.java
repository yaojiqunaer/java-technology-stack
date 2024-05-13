package io.github.yaojiqunaer.exception.annotation;

import io.github.yaojiqunaer.exception.autoconfigure.GlobalExceptionAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(GlobalExceptionAutoConfiguration.class)
public @interface EnableGlobalException {

}
