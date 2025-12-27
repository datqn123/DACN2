package com.example.dacn2.Config;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.URI;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        URI uri = URI.create(elasticsearchUri);
        String host = uri.getHost();
        int port = uri.getPort();

        // Default ports if not specified
        if (port == -1) {
            if ("https".equalsIgnoreCase(uri.getScheme())) {
                port = 443;
            } else {
                port = 9200; // Default HTTP
            }
        }

        String connectionUrl = host + ":" + port;

        var builder = ClientConfiguration.builder()
                .connectedTo(connectionUrl);

        // Handle SSL
        if ("https".equalsIgnoreCase(uri.getScheme())) {
            builder.usingSsl();
        }

        // Handle Basic Auth if present in URI
        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            String[] parts = userInfo.split(":");
            if (parts.length == 2) {
                builder.withBasicAuth(parts[0], parts[1]);
            }
        }

        // Add Interceptor to force JSON headers and remove compatibility headers
        // Add Interceptor to force JSON headers and remove compatibility headers
        builder.withClientConfigurer((RestClientBuilder restClientBuilder) -> restClientBuilder
                .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> httpClientBuilder
                        .addInterceptorLast((HttpRequestInterceptor) (request, context) -> {
                            request.setHeader("Content-Type", "application/json");
                            request.setHeader("Accept", "application/json");
                        })));
        return builder.build();
    }
}
