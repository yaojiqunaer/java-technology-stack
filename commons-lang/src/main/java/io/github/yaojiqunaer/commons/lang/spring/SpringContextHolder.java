package io.github.yaojiqunaer.commons.lang.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    private static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            log.error("boot applicationContext is null, please check the order of applicationContext initialization");
        }
        return applicationContext;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    /**
     * 获取接口的所有实现bean
     *
     * @param clazz bean的接口类
     * @param <T>   接口类
     * @return bean list
     */
    public static <T> Collection<T> getImplBeans(Class<T> clazz) {
        Map<String, T> map = applicationContext.getBeansOfType(clazz);
        return map.values();
    }

    /**
     * 发布Spring事件
     *
     * @param event 事件
     */
    public static void publishEvent(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }

    @Override
    public void destroy() {
        applicationContext = null;
    }

}