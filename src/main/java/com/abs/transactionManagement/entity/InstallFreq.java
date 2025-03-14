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
public class InstallFreq {
    @JsonProperty("Cal")
    private String cal;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("StartDt")
    private String startDt;
    @JsonProperty("HolStat")
    private String holStat;
}
