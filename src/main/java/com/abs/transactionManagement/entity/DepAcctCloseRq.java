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
public class DepAcctCloseRq {
    @JsonProperty("DepAcctId")
    private DepAcctId depAcctId;
    @JsonProperty("CloseModeFlg")
    private String closeModeFlg;
    @JsonProperty("CloseReasonCode")
    private String closeReasonCode;
}
