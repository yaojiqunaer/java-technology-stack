package io.github.yaojiqunaer.resttemplate;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RestTemplateAutoConfiguration.class})
public @interface EnableRestTemplate {
}
