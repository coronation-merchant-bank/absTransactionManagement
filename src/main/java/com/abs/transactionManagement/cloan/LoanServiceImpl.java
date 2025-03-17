package com.abs.transactionManagement.cloan;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final RestClient restClient;
    private final Gson gson;

    @Value("${finacle.liquidateLoan.url}")
    private String liquidateLoanUrl;

    @Override
    public LoanPreLiquidateResponse preLiquidateLoan(LoanPreLiquidateRequest loanPreLiquidateRequest) {
        String xmlRequest = buildPreLiquidateXmlRequest(loanPreLiquidateRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<String> responseEntity = restClient.post()
                .uri(liquidateLoanUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(xmlRequest)
                .retrieve().toEntity(String.class);

        log.info("Finacle Pre-liquidate Loan Response :: httpStatus: {}, body: {}",
                responseEntity.getStatusCode(),
                responseEntity.getBody()
        );
        String xmlResponse = responseEntity.getBody();
        return convertLoanXmlResponseToJson(xmlResponse);
    }

    private String buildPreLiquidateXmlRequest(LoanPreLiquidateRequest loanPreLiquidateRequest) {
        return MessageFormat.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <FIXML xsi:schemaLocation="http://www.finacle.com/fixml executeFinacleScript.xsd" xmlns="http://www.finacle.com/fixml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <Header>
                        <RequestHeader>
                            <MessageKey>
                                <RequestUUID>Req_{0}</RequestUUID>
                                <ServiceRequestId>executeFinacleScript</ServiceRequestId>
                                <ServiceRequestVersion>10.2</ServiceRequestVersion>
                                <ChannelId>COR</ChannelId>
                                <LanguageId></LanguageId>
                            </MessageKey>
                            <RequestMessageInfo>
                                <BankId></BankId>
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
                        <executeFinacleScriptRequest>
                            <ExecuteFinacleScriptInputVO>
                                <requestId>LAA_payoff.scr</requestId>
                            </ExecuteFinacleScriptInputVO>
                            <executeFinacleScript_CustomData>
                                <tranSubType>BI</tranSubType>
                                <LoanAcct>{2}</LoanAcct>
                                <fundAcct>{3}</fundAcct>
                                <tranType>T</tranType>
                            </executeFinacleScript_CustomData>
                        </executeFinacleScriptRequest>
                    </Body>
                </FIXML>
                """,
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                loanPreLiquidateRequest.getLoanAccount(),
                loanPreLiquidateRequest.getFundAccount()
        );
    }

    private LoanPreLiquidateResponse convertLoanXmlResponseToJson(String xmlResponse) {
        // Convert XML to JSONObject
        JSONObject jsonObject = XML.toJSONObject(xmlResponse);

        // Extract required fields
        JSONObject requestMessageKey = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Header")
                .getJSONObject("ResponseHeader")
                .getJSONObject("RequestMessageKey");

        JSONObject executeFinacleScriptCustomData = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Body")
                .getJSONObject("executeFinacleScriptResponse")
                .getJSONObject("executeFinacleScript_CustomData");

        String requestId = requestMessageKey.optString("RequestUUID", "");
        String status = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Header")
                .getJSONObject("ResponseHeader")
                .getJSONObject("HostTransaction")
                .optString("Status", "");

        String code = executeFinacleScriptCustomData.optString("Code", "");
        String message = executeFinacleScriptCustomData.optString("RESULT_MSG", "");

        // Convert to JSON using Gson with pretty printing
        LoanPreLiquidateResponse loanPreLiquidateResponse = LoanPreLiquidateResponse.builder()
                .code(code)
                .status(status)
                .requestId(requestId)
                .message(message)
                .build();

        String jsonOutput = gson.toJson(loanPreLiquidateResponse);

        log.info("PreLiquidation Response Converted to JSON :: {}", jsonOutput);

        return loanPreLiquidateResponse;
    }
}
