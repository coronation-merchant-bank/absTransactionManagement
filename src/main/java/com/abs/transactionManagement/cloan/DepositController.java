package com.abs.transactionManagement.cloan;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deposits")
public class DepositController {
    private final DepositService depositService;

    @PostMapping("/close")
    public DepositLiquidationResponse closeDepositAccount(@RequestBody @Valid DepositLiquidationRequest depositLiquidationRequest) {
        return depositService.preLiquidateDeposit(depositLiquidationRequest);
    }
}
