package com.vk.profileservice.service.impl;

import com.vk.profileservice.dto.VkUserDto;
import com.vk.profileservice.service.VkApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkApiServiceImpl implements VkApiService {

    private final ProducerTemplate producerTemplate;

    @Override
    @Cacheable(value = "vk-users", key = "#userId + ':' + #vkServiceToken")
    public VkUserDto.User getUserInfo(Long userId, String vkServiceToken) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("userId", userId);
        headers.put("vkServiceToken", vkServiceToken);

        return producerTemplate.requestBodyAndHeaders(
                "direct:getUserInfo",
                null,
                headers,
                VkUserDto.User.class);
    }

    @Override
    @Cacheable(value = "vk-memberships", key = "#userId + ':' + #groupId + ':' + #vkServiceToken")
    public boolean checkMembership(Long userId, String groupId, String vkServiceToken) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("userId", userId);
        headers.put("groupId", groupId);
        headers.put("vkServiceToken", vkServiceToken);

        return producerTemplate.requestBodyAndHeaders(
                "direct:checkGroupMembership",
                null,
                headers,
                Boolean.class);
    }
}
