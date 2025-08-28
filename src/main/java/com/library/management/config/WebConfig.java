package com.library.management.config;
import org.springframework.context.annotation.Configuration;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Value;
=======
>>>>>>> upstream/main
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
<<<<<<< HEAD

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
=======
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
>>>>>>> upstream/main
    }
}