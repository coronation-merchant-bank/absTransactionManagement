package com.abs.transactionManagement.cloan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.*;
import org.json.JSONObject;
import org.json.XML;

public class XmlToJsonGsonConverter {
    public static void main(String[] args) {
        String xml = """
                <FIXML xsi:schemaLocation="http://www.finacle.com/fixml executeFinacleScript.xsd"
                    xmlns="http://www.finacle.com/fixml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <Header>
                        <ResponseHeader>
                            <RequestMessageKey>
                                <RequestUUID>Req_1730803476800</RequestUUID>
                                <ServiceRequestId>executeFinacleScript</ServiceRequestId>
                                <ServiceRequestVersion>10.2</ServiceRequestVersion>
                                <ChannelId>COR</ChannelId>
                            </RequestMessageKey>
                            <ResponseMessageInfo>
                                <BankId>01</BankId>
                                <TimeZone>GMT+05:30</TimeZone>
                                <MessageDateTime>2024-11-08T14:32:57.543</MessageDateTime>
                            </ResponseMessageInfo>
                            <UBUSTransaction>
                                <Id/>
                                <Status/>
                            </UBUSTransaction>
                            <HostTransaction>
                                <Id/>
                                <Status>SUCCESS</Status>
                            </HostTransaction>
                            <HostParentTransaction>
                                <Id/>
                                <Status/>
                            </HostParentTransaction>
                            <CustomInfo/>
                        </ResponseHeader>
                    </Header>
                    <Body>
                        <executeFinacleScriptResponse>
                            <ExecuteFinacleScriptOutputVO></ExecuteFinacleScriptOutputVO>
                            <executeFinacleScript_CustomData>
                                <acctId>999530202205101</acctId>
                                <S>N</S>
                                <SuccessOrFailure>Y</SuccessOrFailure>
                                <Code>0000</Code>
                                <RESULT_MSG>Payoff and closure of Loan account 9920230620000093 completed successfully</RESULT_MSG>
                            </executeFinacleScript_CustomData>
                        </executeFinacleScriptResponse>
                    </Body>
                </FIXML>
                """;

        // Convert XML to JSONObject
        JSONObject jsonObject = XML.toJSONObject(xml);

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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String jsonOutput = gson.toJson(new ResponseData(requestId, status, code, message));

        System.out.println(jsonOutput);
    }

    // Helper class for JSON conversion
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class ResponseData {
        private String requestId;
        private String status;
        private String code;
        private String message;
    }
}