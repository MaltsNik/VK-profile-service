package com.vk.profileservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkMembershipDto {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Integer response;
        private Error error;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        @JsonProperty("error_code")
        private Integer errorCode;

        @JsonProperty("error_message")
        private String errorMsg;
    }
}
