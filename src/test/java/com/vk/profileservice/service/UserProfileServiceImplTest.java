package com.vk.profileservice;

import com.vk.profileservice.dto.VkUserDto;
import com.vk.profileservice.exception.VkApiException;
import com.vk.profileservice.model.request.VkUserRequest;
import com.vk.profileservice.model.response.VkUserResponse;
import com.vk.profileservice.service.impl.UserProfileServiceImpl;
import com.vk.profileservice.service.impl.VkApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserProfileServiceImpl")
public class UserProfileServiceImplTest {
    @Mock
    private VkApiServiceImpl vkApiCacheService;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private VkUserRequest testRequest;
    private VkUserDto.User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        testRequest = VkUserRequest.builder()
                .userId(78385L)
                .groupId("93559769")
                .build();

        VkUserDto.User testUser = new VkUserDto.User();
        testUser.setId(78385L);
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setMiddleName("Иванович");

        testToken = "test_vk_token_12345";
    }

    @Test
    @DisplayName("Should return complete user profile with middle name when user is a member")
    void shouldReturnCompleteUserProfileWhenUserIsMember() {
        // Given
        when(vkApiCacheService.getUserInfo(anyLong(), anyString())).thenReturn(testUser);
        when(vkApiCacheService.checkMembership(anyLong(), anyString(), anyString())).thenReturn(true);

        // When
        VkUserResponse result = userProfileService.getUserInfo(testRequest, testToken);

        // Then
        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertEquals("Иванович", result.getMiddleName());
        assertTrue(result.isMember());

        verify(vkApiCacheService).getUserInfo(78385L, testToken);
        verify(vkApiCacheService).checkMembership(78385L, "93559769", testToken);
    }

    @Test
    @DisplayName("Should return user profile without middle name when it's null")
    void shouldReturnProfileWithoutMiddleNameWhenNull() {
        // Given
        testUser.setMiddleName(null);
        when(vkApiCacheService.getUserInfo(anyLong(), anyString())).thenReturn(testUser);
        when(vkApiCacheService.checkMembership(anyLong(), anyString(), anyString())).thenReturn(false);

        // When
        VkUserResponse result = userProfileService.getUserInfo(testRequest, testToken);

        // Then
        assertNotNull(result);
        assertEquals("Иван", result.getFirstName());
        assertEquals("Иванов", result.getLastName());
        assertNull(result.getMiddleName());
        assertFalse(result.isMember());
    }

    @Test
    @DisplayName("Should return user profile without middle name when it's empty")
    void shouldReturnProfileWithoutMiddleNameWhenEmpty() {
        // Given
        testUser.setMiddleName("");
        when(vkApiCacheService.getUserInfo(anyLong(), anyString())).thenReturn(testUser);
        when(vkApiCacheService.checkMembership(anyLong(), anyString(), anyString())).thenReturn(true);

        // When
        VkUserResponse result = userProfileService.getUserInfo(testRequest, testToken);

        // Then
        assertNotNull(result);
        assertNull(result.getMiddleName());
        assertTrue(result.isMember());
    }

    @Test
    @DisplayName("Should throw VkApiException when getUserInfo fails")
    void shouldThrowVkApiExceptionWhenGetUserInfoFails() {
        // Given
        when(vkApiCacheService.getUserInfo(anyLong(), anyString()))
                .thenThrow(new VkApiException("User not found"));

        // When & Then
        VkApiException exception = assertThrows(VkApiException.class, () -> {
            userProfileService.getUserInfo(testRequest, testToken);
        });

        assertTrue(exception.getMessage().contains("User not found") ||
                exception.getMessage().contains("Failed to process VK request"));
        verify(vkApiCacheService).getUserInfo(78385L, testToken);
        verify(vkApiCacheService, never()).checkMembership(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw VkApiException when checkMembership fails")
    void shouldThrowVkApiExceptionWhenCheckMembershipFails() {
        // Given
        when(vkApiCacheService.getUserInfo(anyLong(), anyString())).thenReturn(testUser);
        when(vkApiCacheService.checkMembership(anyLong(), anyString(), anyString()))
                .thenThrow(new VkApiException("Group not found"));

        // When & Then
        VkApiException exception = assertThrows(VkApiException.class, () -> {
            userProfileService.getUserInfo(testRequest, testToken);
        });

        assertTrue(exception.getMessage().contains("Group not found") ||
                exception.getMessage().contains("Failed to process VK request"));
        verify(vkApiCacheService).getUserInfo(78385L, testToken);
        verify(vkApiCacheService).checkMembership(78385L, "93559769", testToken);
    }

    @Test
    @DisplayName("Should handle RuntimeException and wrap it in VkApiException")
    void shouldHandleRuntimeExceptionAndWrapInVkApiException() {
        // Given
        when(vkApiCacheService.getUserInfo(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        VkApiException exception = assertThrows(VkApiException.class, () -> {
            userProfileService.getUserInfo(testRequest, testToken);
        });

        assertEquals("Failed to process VK request", exception.getMessage());
        assertNotNull(exception.getCause());
    }
}
