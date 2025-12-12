package com.ken.infinity.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = uploadProperties.getBaseDir();

        // Map /img/** URLs to serve from BOTH upload directory AND classpath static resources
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + uploadPath + "/img/", "classpath:/static/img/");
    }
}
