package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageKey {
    @JsonProperty("RequestUUID")
    private String requestUUID;
    @JsonProperty("ServiceRequestId")
    private String serviceRequestId;
    @JsonProperty("ServiceRequestVersion")
    private String serviceRequestVersion;
    @JsonProperty("ChannelId")
    private String channelId;
    @JsonProperty("LanguageId")
    private String languageId;
}
