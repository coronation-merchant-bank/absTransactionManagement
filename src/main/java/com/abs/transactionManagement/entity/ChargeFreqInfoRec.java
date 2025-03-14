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
public class ChargeFreqInfoRec {
    @JsonProperty("ChargeType")
    private String chargeType;
    @JsonProperty("DelFlg")
    private String delFlg;
    @JsonProperty("DeductibleFlg")
    private String deductibleFlg;
    @JsonProperty("EventId")
    private String eventId;
    @JsonProperty("AssessFreq")
    private AssessFreq assessFreq;
    @JsonProperty("NextAssessDate")
    private String nextAssessDate;
}
