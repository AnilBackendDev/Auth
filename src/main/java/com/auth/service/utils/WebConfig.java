package com.auth.service.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for serving static resources and handling deep link redirects.
 * This ensures proper routing for deep link HTML pages.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve static HTML files for deep link redirects
        registry.addResourceHandler("/product/**")
                .addResourceLocations("classpath:/static/product/");
        
        registry.addResourceHandler("/category/**")
                .addResourceLocations("classpath:/static/category/");
    }
}

