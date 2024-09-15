package io.github.yaojiqunaer.jpaplus;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * {@link EnableJpaAuditing 开启JPA审计功能}
 */
@Configuration
// JPA实体扫描
@EnableJpaRepositories(basePackages = {
        "com.**"},
        repositoryBaseClass = JpaSimpleBaseRepository.class,
        repositoryFactoryBeanClass = JpaBaseRepositoryFactoryBean.class)
@EnableJpaAuditing
@EnableTransactionManagement
@EnableConfigurationProperties({JpaProperties.class})
public class JpaAutoConfiguration {

    public static final String[] BASE_PACKAGES = {"com.**"};

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        vendorAdapter.setShowSql(jpaProperties.isShowSql());
        vendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(BASE_PACKAGES);
        Map<String, String> properties = jpaProperties.getProperties();
        // 关闭新生成器, 使 javax.persistence.GenerationType.AUTO 对应hibernate的native策略
        properties.putIfAbsent("hibernate.id.new_generator_mappings", "false");
        factory.getJpaPropertyMap().putAll(properties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    /**
     * JPA 审计拦截器实现
     * @return
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new JpaAuditorAware();
    }

}
