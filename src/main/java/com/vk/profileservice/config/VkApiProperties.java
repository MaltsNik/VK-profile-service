package com.vk.profileservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vk.api")
public class VkApiProperties {
    private String baseUrl;
    private String version;
    private int timeout;
    private int maxRetries;
    private int retryDelay;
}
