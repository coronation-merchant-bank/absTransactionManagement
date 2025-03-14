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
public class LoanAcctAddCustomData {
    @JsonProperty("DealerDev")
    private String dealerDev;
    @JsonProperty("ReltnShipMgr")
    private String reltnShipMgr;
    @JsonProperty("TypeOfAdv")
    private String typeOfAdv;
    @JsonProperty("DocFreeText3")
    private String docFreeText3;
    @JsonProperty("ProcessingFee")
    private String processingFee;
    @JsonProperty("DstFee")
    private String dstFee;
    @JsonProperty("SourceSystem")
    private String sourceSystem;
    @JsonProperty("LendingUnit")
    private String lendingUnit;
    @JsonProperty("RediscountCode")
    private String rediscountCode;
    @JsonProperty("CasaAcct")
    private String casaAcct;
    @JsonProperty("DocFreeText1")
    private String docFreeText1;
}
