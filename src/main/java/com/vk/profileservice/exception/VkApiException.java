package com.vk.profileservice.exception;

import lombok.Getter;

@Getter
public class VkApiException extends RuntimeException {
    private Integer errorCode;

    public VkApiException(String message) {
        super(message);
    }

    public VkApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
