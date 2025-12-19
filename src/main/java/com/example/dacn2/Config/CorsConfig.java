package com.example.dacn2.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1. Cho phép các domain cụ thể (không dùng "*" vì xung đột với credentials)
        corsConfiguration.addAllowedOriginPattern("http://localhost:3000");
        corsConfiguration.addAllowedOriginPattern("http://localhost:5173");
        corsConfiguration.addAllowedOriginPattern("http://localhost:8080");
        corsConfiguration.addAllowedOriginPattern("https://*.vercel.app");
        corsConfiguration.addAllowedOriginPattern("https://*.netlify.app");
        corsConfiguration.addAllowedOriginPattern("https://*.onrender.com");

        // 2. Cho phép tất cả các method (GET, POST, PUT, DELETE, OPTIONS...)
        corsConfiguration.addAllowedMethod("*");

        // 3. Cho phép tất cả các Header
        corsConfiguration.addAllowedHeader("*");

        // 4. Cho phép gửi credentials (cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);

        // 5. Expose các headers để frontend có thể đọc
        corsConfiguration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}