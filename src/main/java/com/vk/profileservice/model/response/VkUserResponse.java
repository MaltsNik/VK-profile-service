package com.vk.profileservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "VK user information response")
public class VkUserResponse {

    @JsonProperty("last_name")
    @Schema(description = "User's last name", example = "Иванов")
    private String lastName;

    @JsonProperty("first_name")
    @Schema(description = "User's first name", example = "Иван")
    private String firstName;

    @JsonProperty("middle_name")
    @Schema(description = "User's middle name", example = "Иванович")
    private String middleName;

    @JsonProperty("member")
    @Schema(description = "Indicates if user is a member of the specified group", example = "true")
    private Boolean member;
}
