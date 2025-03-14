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
public class LoanGenDetails {
    @JsonProperty("LoanPeriodMonths")
    private String loanPeriodMonths;
    @JsonProperty("LoanPeriodDays")
    private String loanPeriodDays;
    @JsonProperty("RePmtMethod")
    private String rePmtMethod;
    @JsonProperty("OperAcctId")
    private OperAcctId operAcctId;
    @JsonProperty("PmtPlan")
    private PmtPlan pmtPlan;
    @JsonProperty("LoanAmt")
    private LoanAmt loanAmt;
    @JsonProperty("ReschedParams")
    private ReschedParams reschedParams;
}
