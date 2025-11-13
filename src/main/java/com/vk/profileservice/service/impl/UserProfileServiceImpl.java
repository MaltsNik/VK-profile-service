package com.vk.profileservice.service.impl;

import com.vk.profileservice.dto.VkUserDto;
import com.vk.profileservice.exception.VkApiException;
import com.vk.profileservice.model.request.VkUserRequest;
import com.vk.profileservice.model.response.VkUserResponse;
import com.vk.profileservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserService {

    private final VkApiServiceImpl vkApiCacheService;

    @Override
    public VkUserResponse getUserInfo(VkUserRequest request, String vkServiceToken) {
        log.info("Processing user profile request for userId: {}, groupId: {}",
                request.getUserId(), request.getGroupId());
        try {
            VkUserDto.User user = vkApiCacheService.getUserInfo(request.getUserId(), vkServiceToken);
            boolean isMember = vkApiCacheService.checkMembership(
                    request.getUserId(), request.getGroupId(), vkServiceToken);

            VkUserResponse.VkUserResponseBuilder responseBuilder = VkUserResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .member(isMember);

            if (user.getMiddleName() != null && !user.getMiddleName().isEmpty()) {
                responseBuilder.middleName(user.getMiddleName());
            }
            VkUserResponse response = responseBuilder.build();
            log.info("Successfully processed request for userId: {}", request.getUserId());

            return response;
        } catch (Exception e) {
            log.error("Error processing VK request: {}", e.getMessage(), e);
            if (e instanceof VkApiException) {
                throw (VkApiException) e;
            }
            throw new VkApiException("Failed to process VK request", e);
        }
    }
}
