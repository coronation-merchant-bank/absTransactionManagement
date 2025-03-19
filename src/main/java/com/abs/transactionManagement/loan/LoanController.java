package com.abs.transactionManagement.loan;

import com.abs.transactionManagement.config.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/pre-liquidate")
    public BaseResponse<PreLiquidateLoanResponse> preLiquidateLoan(@RequestBody @Valid PreLiquidateLoanRequest preLiquidateLoanRequest) {
        return loanService.preLiquidateLoan(preLiquidateLoanRequest);
    }
}
