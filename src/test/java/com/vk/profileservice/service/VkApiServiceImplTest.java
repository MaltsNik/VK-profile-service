package com.vk.profileservice.service;

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
    void shouldGetUserInfoSuccessfullyTest() {
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:getUserInfo"),
                isNull(),
                anyMap(),
                eq(VkUserDto.User.class)
        )).thenReturn(testUser);

        VkUserDto.User result = vkApiService.getUserInfo(userId, vkToken);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getMiddleName(), result.getMiddleName());

        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:getUserInfo"),
                isNull(),
                argThat(headers -> {
                    return ((Map<String, Object>) headers).get("userId").equals(userId) &&
                            ((Map<String, Object>) headers).get("vkServiceToken").equals(vkToken);
                }),
                eq(VkUserDto.User.class)
        );
    }

    @Test
    @DisplayName("Should successfully check membership status as true")
    void shouldCheckMembershipStatusAsTrueTest() {
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                anyMap(),
                eq(Boolean.class)
        )).thenReturn(true);

        boolean result = vkApiService.checkMembership(userId, groupId, vkToken);

        assertTrue(result);

        verify(producerTemplate).requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                argThat(headers -> {
                    return ((Map<String, Object>) headers).get("userId").equals(userId) &&
                            ((Map<String, Object>) headers).get("groupId").equals(groupId) &&
                            ((Map<String, Object>) headers).get("vkServiceToken").equals(vkToken);
                }),
                eq(Boolean.class)
        );
    }

    @Test
    @DisplayName("Should successfully check membership status as false")
    void shouldCheckMembershipStatusAsFalseTest() {
        when(producerTemplate.requestBodyAndHeaders(
                eq("direct:checkGroupMembership"),
                isNull(),
                anyMap(),
                eq(Boolean.class)
        )).thenReturn(false);

        boolean result = vkApiService.checkMembership(userId, groupId, vkToken);

        assertFalse(result);
        verify(producerTemplate, times(1)).requestBodyAndHeaders(
                anyString(), isNull(), anyMap(), eq(Boolean.class)
        );
    }
}
