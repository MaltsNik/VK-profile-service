package com.vk.profileservice.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private Long userId;

    public UserNotFoundException(Long userId) {
        super(String.format("User with ID %d not found", userId));
    }

    public UserNotFoundException(Long userId, Throwable cause) {
        super(String.format("User with ID %d not found", userId), cause);
    }
}
