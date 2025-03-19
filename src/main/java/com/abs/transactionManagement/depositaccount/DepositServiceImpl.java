package com.abs.transactionManagement.depositaccount;

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
public class DepositServiceImpl implements DepositService {
    private final HttpService httpService;
    private final Gson gson;

    @Value("${finacle.soap.address}")
    private String soapAddress;

    @Value("${finacle.soap.action}")
    private String soapAction;

    @Override
    public CloseDepositAccResponse closeDepositAccount(CloseDepositAccRequest closeDepositAccRequest) {
        String requestId = UUID.randomUUID().toString();
        String xmlRequest = buildCloseDepositAccXmlRequest(closeDepositAccRequest, requestId);
        log.info("XML Request :: {}", xmlRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.set("SOAPAction", soapAction);
        headers.setContentType(MediaType.APPLICATION_XML);
        ResponseEntity<String> responseEntity = httpService.post(
                xmlRequest,
                headers,
                soapAddress,
                60
        );

        log.info("Finacle Pre-liquidate Deposit Response :: httpStatus: {}, body: {}",
                responseEntity.getStatusCode(),
                responseEntity.getBody()
        );
        String xmlResponse = responseEntity.getBody();
        xmlResponse = FinacleUtil.extractSoapResponseBody(xmlResponse);
        CloseDepositAccResponse response = extractCloseDepositAccResponse(xmlResponse);
        response.setRequestId(requestId);

        return response;
    }

    private String buildCloseDepositAccXmlRequest(CloseDepositAccRequest closeDepositAccRequest, String requestId) {
        String fixml = MessageFormat.format("""
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
                requestId,
                LocalDateTime.now().toString(),
                closeDepositAccRequest.getDepositAccountId(),
                closeDepositAccRequest.getAmount(),
                closeDepositAccRequest.getRepayAccountId()
        );

        return FinacleUtil.wrapSoapRequestEnvelop(fixml);
    }

    private CloseDepositAccResponse extractCloseDepositAccResponse(String xmlResponse) {
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
        if (!StringUtils.hasText(code)) {
            code = status.equalsIgnoreCase("SUCCESS") ? "0000" : "0005";
        }
        if (!StringUtils.hasText(message)) {
            message = executeFinacleScriptCustomData.optString("F", "");
        }

        // Convert to JSON using Gson with pretty printing
        CloseDepositAccResponse response = CloseDepositAccResponse.builder()
                .requestId(requestId)
                .code(code)
                .status(status)
                .message(message)
                .build();

        String jsonOutput = gson.toJson(response);

        log.info("Deposit PreLiquidation Response Converted to JSON :: {}", jsonOutput);

        return response;
    }
}
