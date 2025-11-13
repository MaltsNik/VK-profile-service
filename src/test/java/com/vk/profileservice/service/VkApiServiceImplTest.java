package com.vk.profileservice;

import com.vk.profileservice.dto.VkUserDto;
import com.vk.profileservice.service.impl.VkApiServiceImpl;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for VkApiServiceImpl")
public class VkApiServiceImplTest {
    @Mock
    private ProducerTemplate producerTemplate;

    @InjectMocks
    private VkApiServiceImpl vkApiService;

    private VkUserDto.User testUser;
    private Long userId;
    private String groupId;
    private String vkToken;

    @BeforeEach
    void setUp() {
        testUser = new VkUserDto.User();
        testUser.setId(78385L);
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setMiddleName("Иванович");

        userId = 78385L;
        groupId = "93559769";
        vkToken = "test_token";
    }

    @Test
    @DisplayName("Should successfully get user info from VK API")
    void shouldGetUserInfoSuccessfully() {
        // Given
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:getUserInfo"),
                isNull(),
                anyMap(),
                eq(VkUserDto.User.class)
        )).thenReturn(testUser);

        // When
        VkUserDto.User result = vkApiService.getUserInfo(userId, vkToken);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getMiddleName(), result.getMiddleName());

        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:getUserInfo"),
                isNull(),
                argThat(headers -> {
                    Map<String, Object> headerMap = (Map<String, Object>) headers;
                    return headerMap.get("userId").equals(userId) &&
                            headerMap.get("vkServiceToken").equals(vkToken);
                }),
                eq(VkUserDto.User.class)
        );
    }

    @Test
    @DisplayName("Should successfully check membership status as true")
    void shouldCheckMembershipStatusAsTrue() {
        // Given
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                anyMap(),
                eq(Boolean.class)
        )).thenReturn(true);

        // When
        boolean result = vkApiService.checkMembership(userId, groupId, vkToken);

        // Then
        assertTrue(result);

        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                argThat(headers -> {
                    Map<String, Object> headerMap = (Map<String, Object>) headers;
                    return headerMap.get("userId").equals(userId) &&
                            headerMap.get("groupId").equals(groupId) &&
                            headerMap.get("vkServiceToken").equals(vkToken);
                }),
                eq(Boolean.class)
        );
    }

    @Test
    @DisplayName("Should successfully check membership status as false")
    void shouldCheckMembershipStatusAsFalse() {
        // Given
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                anyMap(),
                eq(Boolean.class)
        )).thenReturn(false);

        // When
        boolean result = vkApiService.checkMembership(userId, groupId, vkToken);

        // Then
        assertFalse(result);
        verify(producerTemplate, times(1)).requestBodyAndHeaders(
                anyString(), isNull(), anyMap(), eq(Boolean.class)
        );
    }

    @Test
    @DisplayName("Should pass correct headers for getUserInfo")
    void shouldPassCorrectHeadersForGetUserInfo() {
        // Given
        Long specificUserId = 12345L;
        String specificToken = "specific_token_xyz";

        when(producerTemplate.requestBodyAndHeaders(
                anyString(), isNull(), anyMap(), eq(VkUserDto.User.class)
        )).thenReturn(testUser);

        // When
        vkApiService.getUserInfo(specificUserId, specificToken);

        // Then
        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:getUserInfo"),
                isNull(),
                argThat(headers -> {
                    Map<String, Object> headerMap = (Map<String, Object>) headers;
                    return headerMap.get("userId").equals(specificUserId) &&
                            headerMap.get("vkServiceToken").equals(specificToken) &&
                            headerMap.size() == 2;
                }),
                eq(VkUserDto.User.class)
        );
    }

    @Test
    @DisplayName("Should pass correct headers for checkMembership")
    void shouldPassCorrectHeadersForCheckMembership() {
        // Given
        Long specificUserId = 99999L;
        String specificGroupId = "test_group_123";
        String specificToken = "token_abc";

        when(producerTemplate.requestBodyAndHeaders(
                anyString(), isNull(), anyMap(), eq(Boolean.class)
        )).thenReturn(true);

        // When
        vkApiService.checkMembership(specificUserId, specificGroupId, specificToken);

        // Then
        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                argThat(headers -> {
                    Map<String, Object> headerMap = (Map<String, Object>) headers;
                    return headerMap.get("userId").equals(specificUserId) &&
                            headerMap.get("groupId").equals(specificGroupId) &&
                            headerMap.get("vkServiceToken").equals(specificToken) &&
                            headerMap.size() == 3;
                }),
                eq(Boolean.class)
        );
    }
}
