package com.abs.transactionManagement.cloan;

public interface DepositService {
    DepositLiquidationResponse preLiquidateDeposit(DepositLiquidationRequest depositLiquidationRequest);
}
