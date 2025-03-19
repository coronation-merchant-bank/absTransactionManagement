package com.abs.transactionManagement.finacle;

import com.abs.transactionManagement.exceptionhandler.CustomException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class FinacleUtil {

    private FinacleUtil() {}

    public static String wrapSoapRequestEnvelop(String request) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                          xmlns:web="http://webservice.fiusb.ci.infosys.com">
                            <soapenv:Header/>
                            <soapenv:Body>
                                <web:executeService>
                                    <web:arg_0_0>
                                        <![CDATA[
                                            %s
                                        ]]>
                                    </web:arg_0_0>
                                </web:executeService>
                            </soapenv:Body>
                        </soapenv:Envelope>
                """.formatted(request);
    }

    public static String extractSoapResponseBody(String response) {
        String unescapedXml = StringEscapeUtils.unescapeXml(response);

        Document doc = Jsoup.parse(unescapedXml, "", Parser.xmlParser());

        // Extract FIXML content
        Elements fixmlElement = doc.select("FIXML");
        if (!fixmlElement.isEmpty()) {
            return fixmlElement.outerHtml();
        } else {
            throw new CustomException("No FIXML data");
        }
    }
}
