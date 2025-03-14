package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EqInstallDetails {
    @JsonProperty("EqInstallFlg")
    private String eqInstallFlg;
    @JsonProperty("EqInstallType")
    private String eqInstallType;
    @JsonProperty("EqInstallFormula")
    private String eqInstallFormula;
}
