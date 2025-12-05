package com.example.pethelper.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:\\Users\\whyco\\Downloads\\PetHelperApplication-main\\PetHelperApplication-main\\uploads\\");

        registry.addResourceHandler("/post_photos/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/post_photos/");

        // Видео постов
        registry.addResourceHandler("/post_videos/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/post_videos/");

        // Аудио постов
        registry.addResourceHandler("/post_audios/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/post_audios/");
    }

}
