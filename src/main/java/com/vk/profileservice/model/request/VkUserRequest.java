package com.vk.profileservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "VK user information request")
public class VkMembershipDto {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @JsonProperty("user_id")
    @Schema(description = "VK user identifier", example = "78385", required = true)
    private Long userId;

    @NotNull(message = "Group ID is required")
    @Positive(message = "Group ID must be positive")
    @JsonProperty("group_id")
    @Schema(description = "VK group identifier", example = "93559769", required = true)
    private Long groupId;
}

