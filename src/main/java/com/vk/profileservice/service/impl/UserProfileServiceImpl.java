package com.vk.profileservice.service;

import com.vk.profileservice.dto.UserProfileResponse;
import com.vk.profileservice.dto.vk.VkUser;
import com.vk.profileservice.exception.UserNotFoundException;
import com.vk.profileservice.model.request.VkUserRequest;
import com.vk.profileservice.model.response.VkUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final ProducerTemplate producerTemplate;


    public VkUserResponse getUserProfile(VkUserRequest request, String vkServiceToken) {
        log.info("Processing user profile request for userId: {}, groupId: {}",
                request.getUserId(), request.getGroupId());

        if (vkServiceToken == null || vkServiceToken.isBlank()) {
            throw new IllegalArgumentException("VK service token отсутствует или пустой");
        }

        // Получаем информацию о пользователе
        VkUser user = vkApiClient.getUserInfo(request.getUserId(), vkServiceToken);

        if (user == null) {
            throw new UserNotFoundException(request.getUserId());
        }

        // Проверяем членство в группе
        boolean isMember = vkApiClient.isGroupMember(
                request.getGroupId(),
                request.getUserId(),
                vkServiceToken
        );

        UserProfileResponse response = UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .member(isMember)
                .build();

        log.info("Successfully processed user profile request for userId: {}", request.getUserId());

        return response;
    }
}

