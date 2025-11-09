package com.example.sdms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // ✅ ADD YOUR NEW RENDER URL HERE
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:9727",
                                "http://127.0.0.1:9727",
                                "https://sdms-project-ready.onrender.com" // <-- THIS IS THE FIX
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}