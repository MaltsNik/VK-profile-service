package com.vk.profileservice.route;

import com.vk.profileservice.config.VkApiProperties;
import com.vk.profileservice.dto.VkMembershipDto;
import com.vk.profileservice.dto.VkUserDto;
import com.vk.profileservice.exception.VkApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VkApiRoute extends RouteBuilder {

    private final VkApiProperties vkApiProperties;

    @Override
    public void configure() {

        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(vkApiProperties.getMaxRetries())
                .redeliveryDelay(vkApiProperties.getRetryDelay()));

        from("direct:getUserInfo")
                .routeId("vk-get-user-info")
                .log("Getting user info for userId: ${header.userId}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple(
                        "user_ids=${header.userId}" +
                                "&fields=middle_name" +
                                "&access_token=${header.vkServiceToken}" +
                                "&v=" + vkApiProperties.getVersion()
                ))
                .toD(vkApiProperties.getBaseUrl() + "/users.get?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, VkUserDto.Response.class)
                .process(exchange -> {
                    VkUserDto.Response response = exchange.getIn().getBody(VkUserDto.Response.class);

                    if (response.getError() != null) {
                        VkUserDto.Error error = response.getError();
                        throw new VkApiException(
                                String.format("VK API Error [%d]: %s", error.getErrorCode(), error.getErrorMsg())
                        );
                    }

                    if (response.getResponse() == null || response.getResponse().isEmpty()) {
                        throw new VkApiException("User not found");
                    }
                    exchange.getIn().setBody(response.getResponse().get(0));
                });

        from("direct:checkGroupMembership")
                .routeId("vk-check-membership")
                .log("Checking membership for userId: ${header.userId} in groupId: ${header.groupId}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_QUERY, simple(
                        "user_id=${header.userId}" +
                                "&group_id=${header.groupId}" +
                                "&access_token=${header.vkServiceToken}" +
                                "&v=" + vkApiProperties.getVersion()
                ))
                .toD(vkApiProperties.getBaseUrl() + "/groups.isMember?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, VkMembershipDto.Response.class)
                .process(exchange -> {
                    VkMembershipDto.Response response = exchange.getIn().getBody(VkMembershipDto.Response.class);

                    if (response.getError() != null) {
                        throw new VkApiException("VK API Error: " + response.getError().getErrorMsg());
                    }
                    Boolean isMember = response.getResponse() != null && response.getResponse() == 1;
                    exchange.getIn().setBody(isMember);
                    log.debug("User membership status: {}", isMember);
                });
    }
}
