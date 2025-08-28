package com.library.management.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadBaseDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the configured upload base directory. When the
        // property 'file.upload-dir' is set to an absolute path, files will be
        // served from that location. Otherwise the default relative 'uploads'
        // directory is used.
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadBaseDir + "/");
    }
}