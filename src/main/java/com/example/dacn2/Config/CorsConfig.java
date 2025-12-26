package com.example.dacn2.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Cho phép các domain từ config (application.properties hoặc ENV)
        String[] origins = allowedOrigins.split(",");
        for (String origin : origins) {
            corsConfiguration.addAllowedOriginPattern(origin.trim());
        }

        // Default fallbacks (an toàn)
        corsConfiguration.addAllowedOriginPattern("https://*.vercel.app");
        corsConfiguration.addAllowedOriginPattern("https://*.netlify.app");
        corsConfiguration.addAllowedOriginPattern("https://*.onrender.com");
        corsConfiguration.addAllowedOriginPattern("https://trip-go-eight.vercel.app/");

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