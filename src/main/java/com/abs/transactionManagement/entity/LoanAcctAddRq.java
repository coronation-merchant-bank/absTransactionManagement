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
public class LoanAcctAddRq {
    @JsonProperty("CustId")
    private CustId custId;
    @JsonProperty("LoanAcctId")
    private LoanAcctId loanAcctId;
    @JsonProperty("LoanAcctGenInfo")
    private LoanAcctGenInfo loanAcctGenInfo;
    @JsonProperty("LoanGenDetails")
    private LoanGenDetails loanGenDetails;
    @JsonProperty("AccountInterestTax")
    private AccountInterestTax accountInterestTax;
    @JsonProperty("LoanIntTblDtlRec")
    private LoanIntTblDtlRec loanIntTblDtlRec;
    @JsonProperty("AcctChargeDetails")
    private AcctChargeDetails acctChargeDetails;
    @JsonProperty("AcctDocumentDtlsRec")
    private AcctDocumentDtlsRec acctDocumentDtlsRec;
}
