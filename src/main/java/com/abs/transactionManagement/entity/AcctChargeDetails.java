package com.abs.transactionManagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcctChargeDetails {
    @JsonProperty("ChargeFreqInfoRec")
    private List<ChargeFreqInfoRec> chargeFreqInfoRec;
}
