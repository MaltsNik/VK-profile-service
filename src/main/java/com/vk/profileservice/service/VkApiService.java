package com.vk.profileservice.service;

import com.vk.profileservice.dto.VkUserDto;

public interface VkApiService {

    VkUserDto.User getUserInfo(Long userId, String vkServiceToken);

    boolean checkMembership(Long userId, String groupId, String vkServiceToken);
}
