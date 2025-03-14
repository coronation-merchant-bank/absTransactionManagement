package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMessageInfo {
    @JsonProperty("BankId")
    private String bankId;
    @JsonProperty("TimeZone")
    private String timeZone;
    @JsonProperty("EntityId")
    private String entityId;
    @JsonProperty("EntityType")
    private String entityType;
    @JsonProperty("ArmCorrelationId")
    private String armCorrelationId;
    @JsonProperty("MessageDateTime")
    private String messageDateTime;
}
