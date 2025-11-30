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
        // 1. Cho phép tất cả các domain truy cập (Hoặc chỉ định rõ http://localhost:3000)
        corsConfiguration.addAllowedOrigin("*");
        // 2. Cho phép tất cả các method (GET, POST, PUT, DELETE...)
        corsConfiguration.addAllowedMethod("*");
        // 3. Cho phép tất cả các Header
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}