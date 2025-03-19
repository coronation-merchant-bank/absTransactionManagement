package com.abs.transactionManagement.loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreLiquidateLoanRequest {
    @NotBlank(message = "loanAccount")
    private String loanAccount;
    @NotBlank(message = "fundAccount")
    private String fundAccount;
}
