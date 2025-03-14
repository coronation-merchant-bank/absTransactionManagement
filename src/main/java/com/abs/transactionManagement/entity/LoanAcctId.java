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
public class LoanAcctId {
    @JsonProperty("AcctId")
    private String acctId;
    @JsonProperty("AcctType")
    private AcctType acctType;
    @JsonProperty("AcctCurr")
    private String acctCurr;
    @JsonProperty("BankInfo")
    private BankInfo bankInfo;
}
