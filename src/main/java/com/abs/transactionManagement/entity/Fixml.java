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
public class Fixml {
    @JsonProperty("Header")
    private Header header;
    @JsonProperty("Body")
    private Body body;
//    private String _xmlns;
//    private String _xmlns_xsi;
//    private String _xsi_schemaLocation;
}
