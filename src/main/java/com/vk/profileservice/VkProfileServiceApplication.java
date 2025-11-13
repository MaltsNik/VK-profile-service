package com.vk.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VkProfileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkProfileServiceApplication.class, args);
    }
}
