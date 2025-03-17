package com.abs.transactionManagement.cloan;

import com.abs.transactionManagement.config.CustomRestTemplate;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositServiceImpl implements DepositService {
    private final RestTemplate restTemplate = CustomRestTemplate.restTemplate();
    private final Gson gson;

    @Value("${finacle.soap.url}")
    private String liquidateDepositUrl;

    @Override
    public DepositLiquidationResponse preLiquidateDeposit(DepositLiquidationRequest depositLiquidationRequest) {
        String xmlRequest = buildPreLiquidateXmlRequest(depositLiquidationRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> httpEntity = new HttpEntity<>(xmlRequest, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                liquidateDepositUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        log.info("Finacle Pre-liquidate Deposit Response :: httpStatus: {}, body: {}",
                responseEntity.getStatusCode(),
                responseEntity.getBody()
        );
        String xmlResponse = responseEntity.getBody();
        return convertLoanXmlResponseToJson(xmlResponse);
    }

    private String buildPreLiquidateXmlRequest(DepositLiquidationRequest depositLiquidationRequest) {
        return MessageFormat.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <FIXML xsi:schemaLocation="http://www.finacle.com/fixml DepAcctClose.xsd" xmlns="http://www.finacle.com/fixml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <Header>
                        <RequestHeader>
                            <MessageKey>
                                <RequestUUID>{0}</RequestUUID>
                                <ServiceRequestId>DepAcctClose</ServiceRequestId>
                                <ServiceRequestVersion>10.2</ServiceRequestVersion>
                                <ChannelId>COR</ChannelId>
                                <LanguageId></LanguageId>
                            </MessageKey>
                            <RequestMessageInfo>
                                <BankId>01</BankId>
                                <TimeZone></TimeZone>
                                <EntityId></EntityId>
                                <EntityType></EntityType>
                                <ArmCorrelationId></ArmCorrelationId>
                                <MessageDateTime>{1}</MessageDateTime>
                            </RequestMessageInfo>
                            <Security>
                                <Token>
                                    <PasswordToken>
                                        <UserId></UserId>
                                        <Password></Password>
                                    </PasswordToken>
                                </Token>
                                <FICertToken></FICertToken>
                                <RealUserLoginSessionId></RealUserLoginSessionId>
                                <RealUser></RealUser>
                                <RealUserPwd></RealUserPwd>
                                <SSOTransferToken></SSOTransferToken>
                            </Security>
                        </RequestHeader>
                    </Header>
                    <Body>
                        <DepAcctCloseRequest>
                            <DepAcctCloseRq>
                                <DepAcctId>
                                    <AcctId>{2}</AcctId>
                                    <AcctType>
                                        <SchmType>TDA</SchmType>
                                    </AcctType>
                                </DepAcctId>
                                <CloseModeFlg>Y</CloseModeFlg>
                                <CloseAmt>
                                    <amountValue>{3}</amountValue>
                                    <currencyCode>NGN</currencyCode>
                                </CloseAmt>
                                <RepayAcctId>
                                    <AcctId>{4}</AcctId>
                                </RepayAcctId>
                            </DepAcctCloseRq>
                            <DepAcctClose_CustomData>
                                <WITHDRWLAMT1>{3}</WITHDRWLAMT1>
                                <WCRNCY>NGN</WCRNCY>
                            </DepAcctClose_CustomData>
                        </DepAcctCloseRequest>
                    </Body>
                </FIXML>
                """,
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                depositLiquidationRequest.getDepositAccountId(),
                depositLiquidationRequest.getAmount(),
                depositLiquidationRequest.getRepayAccountId()
        );
    }

    private DepositLiquidationResponse convertLoanXmlResponseToJson(String xmlResponse) {
        // Convert XML to JSONObject
        JSONObject jsonObject = XML.toJSONObject(xmlResponse);

        // Extract required fields
        JSONObject requestMessageKey = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Header")
                .getJSONObject("ResponseHeader")
                .getJSONObject("RequestMessageKey");

        String requestId = requestMessageKey.optString("RequestUUID", "");
        String status = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Header")
                .getJSONObject("ResponseHeader")
                .getJSONObject("HostTransaction")
                .optString("Status", "");

        String depositAccountId = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Body")
                .getJSONObject("OperAcctCloseResponse")
                .getJSONObject("OperAcctCloseRs")
                .optString("AcctId", "");

        // Convert to JSON using Gson with pretty printing
        DepositLiquidationResponse depositLiquidationResponse = DepositLiquidationResponse.builder()
                .status(status)
                .requestId(requestId)
                .accountId(depositAccountId)
                .build();

        String jsonOutput = gson.toJson(depositLiquidationResponse);

        log.info("Deposit PreLiquidation Response Converted to JSON :: {}", jsonOutput);

        return depositLiquidationResponse;
    }
}
