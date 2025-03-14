package com.abs.transactionManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocalRequest {
    private String acctId;
    private String userId; //FIVUSR
    private String password; //nullable
}
