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
public class FlowAmt {
    @JsonProperty("AmountValue")
    private String amountValue;
    @JsonProperty("CurrencyCode")
    private String currencyCode;
}
