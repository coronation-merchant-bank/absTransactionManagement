package com.abs.transactionManagement.loan;

import com.abs.transactionManagement.config.BaseResponse;
import com.abs.transactionManagement.exceptionhandler.CustomException;
import com.abs.transactionManagement.finacle.FinacleUtil;
import com.abs.transactionManagement.services.HttpService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final HttpService httpService;
    private final Gson gson;

    @Value("${finacle.soap.address}")
    private String soapAddress;

    @Value("${finacle.soap.action}")
    private String soapAction;

    @Override
    public BaseResponse<PreLiquidateLoanResponse> preLiquidateLoan(PreLiquidateLoanRequest preLiquidateLoanRequest) {
        String requestId = UUID.randomUUID().toString();
        String xmlRequest = buildPreLiquidateXmlRequest(preLiquidateLoanRequest, requestId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set("SOAPAction", soapAction);
        ResponseEntity<String> responseEntity = httpService.post(
                xmlRequest,
                headers,
                soapAddress,
                60
        );

        log.info("Finacle Pre-liquidate Loan Response :: httpStatus: {}, body: {}",
                responseEntity.getStatusCode(),
                responseEntity.getBody()
        );
        String xmlResponse = responseEntity.getBody();
        xmlResponse = FinacleUtil.extractSoapResponseBody(xmlResponse);
        PreLiquidateLoanResponse response = convertLoanXmlResponseToJson(xmlResponse);
        response.setRequestId(requestId);

        return BaseResponse.<PreLiquidateLoanResponse>builder()
                .flag(true)
                .code("00")
                .message(response.getMessage())
                .data(response)
                .build();
    }

    private String buildPreLiquidateXmlRequest(PreLiquidateLoanRequest preLiquidateLoanRequest, String requestId) {
        String fixmlRequest = MessageFormat.format("""
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
                requestId,
                LocalDateTime.now().toString(),
                preLiquidateLoanRequest.getLoanAccount(),
                preLiquidateLoanRequest.getFundAccount()
        );
        return FinacleUtil.wrapSoapRequestEnvelop(fixmlRequest);
    }

    private PreLiquidateLoanResponse convertLoanXmlResponseToJson(String xmlResponse) {
        // Convert XML to JSONObject
        JSONObject jsonObject = XML.toJSONObject(xmlResponse);

        // Extract required fields
        JSONObject requestMessageKey = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Header")
                .getJSONObject("ResponseHeader")
                .getJSONObject("RequestMessageKey");

        JSONObject error = jsonObject.getJSONObject("FIXML")
                .getJSONObject("Body")
                .optJSONObject("Error", null);

        if (error != null) {
            JSONArray errorDetails = error.getJSONObject("FIBusinessException")
                    .getJSONArray("ErrorDetail");
            int l = errorDetails.length();
            List<String> codes = new ArrayList<>();
            List<String> messages = new ArrayList<>();
            for (int i = 0; i < l; i++) {
                JSONObject errorDetail = errorDetails.getJSONObject(i);
                codes.add(errorDetail.optString("ErrorCode", ""));
                messages.add(errorDetail.optString("ErrorDesc", ""));
            }
            String code = String.join(" | ", codes);
            String message = String.join(" | ", messages);

            code = StringUtils.hasText(code) ? code : "0005";
            message = StringUtils.hasText(message) ? message : "Request error";
            throw new CustomException(HttpStatus.BAD_REQUEST, code, message);
        }

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
        PreLiquidateLoanResponse loanPreLiquidateResponse = PreLiquidateLoanResponse.builder()
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
