package io.github.yaojiqunaer.commons.lang.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {
    private final static String YML_SUFFIX = ".yml";
    private final static String YAML_SUFFIX = ".yaml";

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        String resourceName = Optional.ofNullable(name).orElse(encodedResource.getResource().getFilename());
        try {
            if (Objects.requireNonNull(resourceName).endsWith(YML_SUFFIX) || resourceName.endsWith(YAML_SUFFIX)) {
                //yaml资源文件
                List<PropertySource<?>> yamlSources = new YamlPropertySourceLoader().load(resourceName,
                        encodedResource.getResource());
                return yamlSources.get(0);
            } else {
                //返回空的Properties
                return new PropertiesPropertySource(resourceName, new Properties());
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            String message = e.getMessage();
            if (message.contains(FileNotFoundException.class.getName())) {
                throw new FileNotFoundException(message.replace(FileNotFoundException.class.getName(),
                        StringUtils.EMPTY));
            }
        }
        return new PropertiesPropertySource(resourceName, new Properties());
    }

}