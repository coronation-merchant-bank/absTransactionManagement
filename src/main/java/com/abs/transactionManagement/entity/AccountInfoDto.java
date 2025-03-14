package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfoDto {
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime maturityDate;
    private BigDecimal amount;
    private BigDecimal contractRate;
    private String businessUnit;
    private String relationshipOfficer;
    private LocalDateTime openEffectiveDate;
    private BigDecimal depositAmount;
    private BigDecimal clrBalAmt;
    private String loanId;
    private String investmentId;
    private String account;
    private String acctNumber;
    private BigDecimal fullRate;


    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }

    public BigDecimal getFullRate() {
        return fullRate;
    }

    public void setFullRate(BigDecimal fullRate) {
        this.fullRate = fullRate;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }

    public String getAccount() {
        return account;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getContractRate() {
        return contractRate;
    }

    public void setContractRate(BigDecimal contractRate) {
        this.contractRate = contractRate;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getRelationshipOfficer() {
        return relationshipOfficer;
    }

    public void setRelationshipOfficer(String relationshipOfficer) {
        this.relationshipOfficer = relationshipOfficer;
    }

    public LocalDateTime getOpenEffectiveDate() {
        return openEffectiveDate;
    }

    public void setOpenEffectiveDate(LocalDateTime openEffectiveDate) {
        this.openEffectiveDate = openEffectiveDate;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDateTime maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getClrBalAmt() {
        return clrBalAmt;
    }

    public void setClrBalAmt(BigDecimal clrBalAmt) {
        this.clrBalAmt = clrBalAmt;
    }

    public AccountInfoDto(String account, String customerName, LocalDateTime startDate,  BigDecimal amount, BigDecimal contractRate, String businessUnit, String relationshipOfficer, LocalDateTime openEffectiveDate, LocalDateTime maturityDate, BigDecimal depositAmount, BigDecimal clrBalAmt, String loanId, String acctNumber, BigDecimal fullRate, String investmentId) {

        this.account = account;
        this.customerName = customerName;
        this.startDate = startDate;
        this.amount = amount;
        this.contractRate = contractRate;
        this.businessUnit = businessUnit;
        this.relationshipOfficer = relationshipOfficer;
        this.openEffectiveDate = openEffectiveDate;
        this.maturityDate = maturityDate;
        this.depositAmount = depositAmount;
        this.clrBalAmt = clrBalAmt;
        this.loanId = loanId;
        this.acctNumber = acctNumber;
        this.fullRate = fullRate;
        this.investmentId = investmentId;
    }

    public AccountInfoDto() {
    }
}
