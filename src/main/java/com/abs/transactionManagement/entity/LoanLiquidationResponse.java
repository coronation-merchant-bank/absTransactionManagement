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
public class LoanLiquidationResponse {
//
//    [  {    "customerName": "EKUNDAYO O FEYISAYO",
//            "startDate": "2015-06-01T00:00:00",
//            "maturityDate": "2018-10-20T00:00:00",
//            "amount": 590277.82,
//            "loanId": "9920150601000374",    "" +
//            "fullRate": 5  }
//]
    private String customerName;
    private String startDate;
    private String maturityDate;
    private BigDecimal amount;
    private String loanId;
    private BigDecimal fullRate;
    private BigDecimal clrBalAmt;
}
