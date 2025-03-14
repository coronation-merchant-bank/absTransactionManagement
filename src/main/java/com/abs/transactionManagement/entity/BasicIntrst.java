package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BasicIntrst {
    @JsonProperty("AcctDrPrefPcnt")
    private AcctDrPrefPcnt acctDrPrefPcnt;
}
