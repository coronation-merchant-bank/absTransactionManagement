package com.abs.transactionManagement.depositaccount;

import com.abs.transactionManagement.config.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deposit-accounts")
public class DepositController {
    private final DepositService depositService;

    @PostMapping("/close")
    public BaseResponse<CloseDepositAccResponse> closeDepositAccount(@RequestBody @Valid CloseDepositAccRequest closeDepositAccRequest) {
        return depositService.closeDepositAccount(closeDepositAccRequest);
    }
}
