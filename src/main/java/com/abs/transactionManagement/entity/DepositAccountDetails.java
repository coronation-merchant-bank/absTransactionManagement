package com.abs.transactionManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositAccountDetails {
    private String customerName;
    private String maturityDate;
    private BigDecimal fullRate;
    private String businessUnit;
    private String relationshipOfficer;
    private String openEffectiveDate;
    private BigDecimal depositAmount;
    private String investmentId;
    private String acctNumber;
}
