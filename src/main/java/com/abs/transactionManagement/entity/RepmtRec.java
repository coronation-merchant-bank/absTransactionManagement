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
public class RepmtRec {
    @JsonProperty("InstallmentId")
    private String installmentId;
    @JsonProperty("InstallStartDt")
    private String installStartDt;
    @JsonProperty("InstallFreq")
    private InstallFreq installFreq;
    @JsonProperty("IntFreq")
    private IntFreq intFreq;
    @JsonProperty("NoOfInstall")
    private String noOfInstall;
    @JsonProperty("IntStartDt")
    private String intStartDt;
    @JsonProperty("FlowAmt")
    private FlowAmt flowAmt;
}
