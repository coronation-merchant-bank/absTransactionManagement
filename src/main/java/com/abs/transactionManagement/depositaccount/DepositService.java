package com.abs.transactionManagement.depositaccount;

import com.abs.transactionManagement.config.BaseResponse;

public interface DepositService {
    BaseResponse<CloseDepositAccResponse> closeDepositAccount(CloseDepositAccRequest closeDepositAccRequest);
}
