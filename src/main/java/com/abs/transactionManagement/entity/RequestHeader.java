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
public class RequestHeader {
    @JsonProperty("MessageKey")
    private MessageKey messageKey;
    @JsonProperty("RequestMessageInfo")
    private RequestMessageInfo requestMessageInfo;
    @JsonProperty("Security")
    private Security security;
}
