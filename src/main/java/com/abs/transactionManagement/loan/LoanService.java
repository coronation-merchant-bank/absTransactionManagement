package com.abs.transactionManagement.loan;

import com.abs.transactionManagement.config.BaseResponse;

public interface LoanService {
    BaseResponse<PreLiquidateLoanResponse> preLiquidateLoan(PreLiquidateLoanRequest preLiquidateLoanRequest);
}
