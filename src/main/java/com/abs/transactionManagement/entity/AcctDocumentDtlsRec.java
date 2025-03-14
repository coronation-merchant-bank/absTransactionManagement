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
public class AcctDocumentDtlsRec {
    @JsonProperty("DocRefCode")
    private String docRefCode;
    @JsonProperty("DocScanFlg")
    private String docScanFlg;
    @JsonProperty("DueDt")
    private String dueDt;
    @JsonProperty("DocFreeText1")
    private String docFreeText1;
}
