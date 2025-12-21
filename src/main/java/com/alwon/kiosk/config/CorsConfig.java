package com.alwon.kiosk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                                .allowedOrigins(
                                                "http://localhost:5174", // Vite dev server
                                                "http://localhost:5173", // Alternative port
                                                "http://127.0.0.1:5174",
                                                "http://127.0.0.1:5173",
                                                "file://" // For Android WebView
                                )
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                                .allowedHeaders("*")
                                .allowCredentials(true)
                                .maxAge(3600);
        }
}
