package io.github.yaojiqunaer.commons.lang.spring.config;

import io.github.yaojiqunaer.commons.lang.spring.SpringContextHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        SpringContextHolder.class,
})
public class SpringUtilAutoConfiguration {

}
