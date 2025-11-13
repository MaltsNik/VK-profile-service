package com.vk.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkUserDto {
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private List<User> response;
        private Error error;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("middle_name")
        private String middleName;

        @JsonProperty("can_access_closed")
        private Boolean canAccessClosed;

        @JsonProperty("is_closed")
        private Boolean isClosed;

        private Integer deactivated;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        @JsonProperty("error_code")
        private Integer errorCode;

        @JsonProperty("error_msg")
        private String errorMsg;

    }
}
