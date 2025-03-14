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
public class GenLedgerSubHead {
    @JsonProperty("GenLedgerSubHeadCode")
    private String genLedgerSubHeadCode;
    @JsonProperty("CurCode")
    private String curCode;
}
