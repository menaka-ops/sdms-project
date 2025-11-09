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
                // ✅ --- FINAL VERSION ---
                // This allows your Render frontend to connect.
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:9727",
                                "http://127.0.0.1:9727",
                                "https://sdms-project-ready.onrender.com" // Your Render URL
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}