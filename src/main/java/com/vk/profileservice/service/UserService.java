package com.vk.profileservice.service;

import com.vk.profileservice.model.request.VkUserRequest;
import com.vk.profileservice.model.response.VkUserResponse;

public interface UserService {

    VkUserResponse getUserInfo(VkUserRequest request, String vkServiceToken);

}
