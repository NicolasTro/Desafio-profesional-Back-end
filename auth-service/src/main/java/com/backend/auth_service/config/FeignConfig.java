package com.backend.auth_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${internal.key}")
    private String internalKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("X-INTERNAL-KEY", internalKey);
    }
}
