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
public class LoanAcctGenInfo {
    @JsonProperty("GenLedgerSubHead")
    private GenLedgerSubHead genLedgerSubHead;
    @JsonProperty("AcctName")
    private String acctName;
    @JsonProperty("AcctShortName")
    private String acctShortName;
    @JsonProperty("AcctStmtMode")
    private String acctStmtMode;
}
