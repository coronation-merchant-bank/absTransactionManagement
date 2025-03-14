package com.abs.transactionManagement.controller;



import com.abs.transactionManagement.entity.*;
import com.abs.transactionManagement.services.AbsService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("abs")
@Slf4j
public class AbsController {


    @Autowired
    private AbsService service;

    @GetMapping("fetchLoan")
    public ResponseEntity<List<AccountInfoDto>> fetchLoan(@RequestParam String loanId){
        return ResponseEntity.ofNullable(service.getAccountInfo(loanId));
    }

    @GetMapping("fetchDeposit")
    public ResponseEntity<List<DepositAccountDetails>> depositAmount(@RequestParam String investmentId){
        return ResponseEntity.ofNullable(service.getDepositAccountInfo(investmentId));
    }

    @GetMapping("confirmLoanLiquidation")
    public ResponseEntity<List<LoanLiquidationResponse>> zeroBalAcct(@RequestParam String loanId){
        return ResponseEntity.ofNullable(service.confirmLoanLiquidation(loanId));
    }

    @GetMapping("confirmInvLiquidation")
    public ResponseEntity<List<AccountInfoDto>> zeroDepositBal(@RequestParam String investmentId){
        return ResponseEntity.ofNullable(service.getZeroBalanceDepositInfo(investmentId));
    }

//    @GetMapping("confirmDepLiquidation")
//    public ResponseEntity<List<AccountInfoDto>> confirmDepositLiquidation(@RequestParam String investmentId){
//        return ResponseEntity.ofNullable(service.confirmDepositLiquidation(investmentId));
//    }

    @PostMapping("closeDepAcct")
    public ResponseEntity<?> closeDepositAccount(@RequestBody LocalRequest request){
        return ResponseEntity.ofNullable(service.closeDepositAccount(request));
    }

    @PostMapping("addLoan")
    public ResponseEntity<?> addLoan(@RequestBody Fixml request){
        log.info("entering controller");
        return ResponseEntity.ok(service.addLoan(request));
    }

    @PostMapping("addInvestment")
    public ResponseEntity<?> addInvestment(@RequestBody Fixml request){
       return null;
    }

}
