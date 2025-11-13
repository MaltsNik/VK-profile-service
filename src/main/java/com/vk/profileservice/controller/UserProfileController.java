package com.vk.profileservice.controller;

import com.vk.profileservice.model.request.VkUserRequest;
import com.vk.profileservice.model.response.VkUserResponse;
import com.vk.profileservice.service.impl.UserProfileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API для получения информации о пользователях VK")
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;

    @PostMapping("/profile")
    @Operation(
            summary = "Получить профиль пользователя VK",
            description = "Возвращает ФИО пользователя и признак членства в группе",
            security = @SecurityRequirement(name = "basicAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = VkUserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "500", description = "Ошибка VK API")
            }
    )
    public ResponseEntity<VkUserResponse> getUserProfile(
            @Parameter(description = "VK Service Token", required = true)
            @RequestHeader("vk_service_token")
            @NotBlank(message = "VK service token is required")
            String vkServiceToken,

            @Parameter(description = "Request body with user and group IDs", required = true)
            @Valid
            @RequestBody VkUserRequest request
    ) {
        log.info("Received user profile request for userId: {}, groupId: {}",
                request.getUserId(), request.getGroupId());

        VkUserResponse response = userProfileService.getUserInfo(request, vkServiceToken);

        return ResponseEntity.ok(response);
    }
}
