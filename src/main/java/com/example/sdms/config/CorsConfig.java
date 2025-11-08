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
                // ✅ ADD THE LIVE RENDER DOMAIN TO ALLOW COMMUNICATION
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:9727",
                                "http://127.0.0.1:9727",
                                "https://sdms-project-2-b8fe.onrender.com" // <-- THIS FIXES THE MOBILE/LIVE ISSUE
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}