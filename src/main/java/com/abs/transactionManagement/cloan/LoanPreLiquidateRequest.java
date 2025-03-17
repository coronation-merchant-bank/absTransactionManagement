package com.abs.transactionManagement.cloan;

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
public class LoanPreLiquidateRequest {
    @NotNull(message = "requestId is required")
    private String requestId;
    @NotBlank(message = "loanAccount")
    private String loanAccount;
    @NotBlank(message = "fundAccount")
    private String fundAccount;
}
