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
public class LoanAcctAddRequest {
    @JsonProperty("LoanAcctAddRq")
    private LoanAcctAddRq loanAcctAddRq;
    @JsonProperty("LoanAcctAddCustomData")
    private LoanAcctAddCustomData loanAcctAddCustomData;
}
