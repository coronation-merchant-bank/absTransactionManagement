package com.abs.transactionManagement.services;

import com.abs.transactionManagement.config.CustomRestTemplate;
import com.abs.transactionManagement.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AbsService {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Value("${depositCloseUrl}")
    private String bankingUrl;

    @Value("${addLoanUrl}")
    private String addLoanUrl;


    private final RestTemplate restTemplate = CustomRestTemplate.restTemplate();

    public AbsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AccountInfoDto> getAccountInfo(String accountNum) {
        String sql = """
                SELECT DISTINCT
                foracid AS loanId, 
                (SELECT foracid FROM tbaadm.gam WHERE acid = b.op_acid) AS acctNumber,    
                acct_name AS customerName,    
                b.EI_PERD_START_DATE AS startDate,    
                b.EI_PERD_END_DATE AS maturityDate,    
                DIS_AMT AS amount,    
                (SELECT free_code_1 FROM tbaadm.gac WHERE acid = b.op_acid) AS businessUnit,   
                (SELECT interest_rate FROM tbaadm.eit x WHERE x.entity_id = a.acid) AS fullRate,
                (SELECT ref_desc FROM tbaadm.rct 
                 WHERE ref_code = (SELECT free_code_2 FROM tbaadm.gac WHERE acid = b.op_acid )
                 AND ROWNUM = 1) AS relationshipOfficer 
            FROM 
                tbaadm.gam a 
            JOIN 
                tbaadm.lam b ON a.acid = b.acid
            WHERE 
                foracid = ?
            """;

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            AccountInfoDto dto = new AccountInfoDto();
            dto.setLoanId(rs.getString("loanId"));  // Matches alias in SQL
            dto.setAcctNumber(rs.getString("acctNumber"));  // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setStartDate(rs.getTimestamp("startDate").toLocalDateTime()); // Matches alias in SQL
            dto.setMaturityDate(rs.getTimestamp("maturityDate").toLocalDateTime()); // Matches alias in SQL
            dto.setAmount(rs.getBigDecimal("amount")); // Matches alias in SQL
            dto.setBusinessUnit(rs.getString("businessUnit")); // Matches alias in SQL
            dto.setFullRate(rs.getBigDecimal("fullRate")); // Matches alias in SQL
            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
            return dto;
        });
    }

    public List<DepositAccountDetails> getDepositAccountInfo(String accountNum) {
        String sql = """
                SELECT DISTINCT\s
                                                                         a.foracid AS investmentId,
                                                                          (SELECT x.foracid FROM tbaadm.gam x WHERE x.acid = b.repayment_acid) AS acctNumber,   \s
                                                                          acct_name AS customerName,\s
                                                                          b.open_effective_date AS openEffectiveDate,   \s
                                                                          b.maturity_date AS maturityDate,\s
                                                                          b.deposit_amount AS depositAmount,\s
                                                                          (SELECT y.interest_rate FROM tbaadm.eit y WHERE y.entity_id = a.acid) AS contractRate,     \s
                                                                          (SELECT gac.free_code_1 FROM tbaadm.gac WHERE gac.acid = b.repayment_acid) AS businessUnit,   \s
                                                                          (SELECT ref_desc FROM tbaadm.rct\s
                                                                           WHERE ref_code = (SELECT gac.free_code_2 FROM tbaadm.gac WHERE gac.acid = b.repayment_acid)     \s
                                                                           AND ROWNUM = 1) AS relationshipOfficer\s
                                                                      FROM\s
                                                                          tbaadm.gam a,\s
                                                                          tbaadm.tam b\s
                                                                      WHERE\s
                                                                          a.acid = b.acid\s
                                                                          AND a.foracid = ?""";

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            DepositAccountDetails dto = new DepositAccountDetails();
            dto.setInvestmentId(rs.getString("investmentId"));  // Matches alias in SQL
            dto.setAcctNumber(rs.getString("acctNumber"));      // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setOpenEffectiveDate(String.valueOf(rs.getTimestamp("openEffectiveDate").toLocalDateTime())); // Matches alias in SQL
            dto.setMaturityDate(String.valueOf(rs.getTimestamp("maturityDate").toLocalDateTime()));          // Matches alias in SQL
            dto.setDepositAmount(rs.getBigDecimal("depositAmount")); // Matches alias in SQL
            dto.setFullRate(rs.getBigDecimal("contractRate"));   // Matches alias in SQL
            dto.setBusinessUnit(rs.getString("businessUnit"));       // Matches alias in SQL
            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
            return dto;
        });
    }

    public List<AccountInfoDto> getZeroBalanceAccountInfo(String accountNum) {
        String sql = """
        SELECT DISTINCT
            foracid AS Account,
            acct_name AS customerName,
            b.EI_PERD_START_DATE,
            b.EI_PERD_END_DATE,
            a.clr_bal_amt,
            full_rate AS contractRate,
            (SELECT free_code_1 FROM tbaadm.gac WHERE acid = b.op_acid) AS businessUnit,
            (SELECT ref_desc 
             FROM tbaadm.rct 
             WHERE ref_code = (
                 SELECT free_code_2 
                 FROM tbaadm.gac 
                 WHERE acid = b.op_acid
             ) 
             FETCH FIRST 1 ROWS ONLY) AS relationshipOfficer
        FROM tbaadm.gam a
        JOIN tbaadm.lam b ON a.acid = b.acid
        JOIN tbaadm.idt c ON b.acid = c.ENTITY_ID
        WHERE foracid = ?
        AND clr_bal_amt = 0
    """;

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            AccountInfoDto dto = new AccountInfoDto();
            dto.setAccount(rs.getString("Account"));  // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setStartDate(rs.getTimestamp("EI_PERD_START_DATE").toLocalDateTime()); // No alias needed
            dto.setMaturityDate(rs.getTimestamp("EI_PERD_END_DATE").toLocalDateTime()); // No alias needed
            dto.setClrBalAmt(rs.getBigDecimal("clr_bal_amt")); // No alias needed
            dto.setContractRate(rs.getBigDecimal("contractRate")); // Matches alias in SQL
            dto.setBusinessUnit(rs.getString("businessUnit")); // Matches alias in SQL
            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
            return dto;
        });
    }

    public List<AccountInfoDto> getZeroBalanceDepositInfo(String accountNum) {
        String sql = """
        SELECT DISTINCT
            foracid AS Account,
            acct_name AS customerName,
            b.open_effective_date,
            b.maturity_date,
            clr_bal_amt,
            full_rate AS contractRate,
            (SELECT free_code_1 FROM tbaadm.gac WHERE acid = b.repayment_acid) AS businessUnit,
            (SELECT ref_desc 
             FROM tbaadm.rct 
             WHERE ref_code = (
                 SELECT free_code_2 
                 FROM tbaadm.gac 
                 WHERE acid = b.repayment_acid
             ) 
             FETCH FIRST 1 ROWS ONLY) AS relationshipOfficer
        FROM tbaadm.gam a
        JOIN tbaadm.tam b ON a.acid = b.acid
        JOIN tbaadm.idt c ON b.acid = c.ENTITY_ID
        WHERE foracid = ?
        AND clr_bal_amt = 0
    """;

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            AccountInfoDto dto = new AccountInfoDto();
            dto.setInvestmentId(rs.getString("Account"));  // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setOpenEffectiveDate(rs.getTimestamp("open_effective_date").toLocalDateTime()); // No alias needed
            dto.setMaturityDate(rs.getTimestamp("maturity_date").toLocalDateTime()); // No alias needed
            dto.setClrBalAmt(rs.getBigDecimal("clr_bal_amt")); // No alias needed
            dto.setContractRate(rs.getBigDecimal("contractRate")); // Matches alias in SQL
            dto.setBusinessUnit(rs.getString("businessUnit")); // Matches alias in SQL
            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
//            dto.setDepositAmount(rs.getBigDecimal("depositAmount"));
//            dto.setAcctNumber(rs.getString("acctNumber"));
        
            return dto;
        });
    }

    public List<LoanLiquidationResponse> confirmLoanLiquidation(String accountNum){
        String sql = """
    SELECT DISTINCT
        foracid AS loanId,
        (SELECT foracid FROM tbaadm.gam WHERE acid = b.op_acid) AS acctNumber,
        acct_name AS customerName,
        b.EI_PERD_START_DATE AS startDate,
        b.EI_PERD_END_DATE AS maturityDate,
        DIS_AMT AS amount,
        (SELECT free_code_1 FROM tbaadm.gac WHERE acid = b.op_acid) AS businessUnit,
        (SELECT full_rate 
         FROM tbaadm.idt x 
         WHERE x.entity_id = c.entity_id 
         AND serial_num = (SELECT MAX(serial_num) FROM tbaadm.idt WHERE entity_id = x.entity_id) 
         AND ROWNUM = 1) AS fullRate,
        (SELECT ref_desc 
         FROM tbaadm.rct 
         WHERE ref_code = (
             SELECT free_code_2 
             FROM tbaadm.gac 
             WHERE acid = b.op_acid
         ) 
         AND ROWNUM = 1) AS relationshipOfficer
    FROM tbaadm.gam a
    JOIN tbaadm.lam b ON a.acid = b.acid
    JOIN tbaadm.idt c ON b.acid = c.entity_id
    WHERE ((payoff_flg = 'Y' AND payoff_date IS NOT NULL) 
           OR (acct_cls_flg = 'Y' AND acct_cls_date IS NOT NULL) 
           OR clr_bal_amt = 0) 
      AND foracid = ?
    """;

        

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            LoanLiquidationResponse dto = new LoanLiquidationResponse();
            dto.setLoanId(rs.getString("loanId"));  // Matches alias in SQL
//            dto.setAcctNumber(rs.getString("acctNumber"));  // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setStartDate(String.valueOf(rs.getTimestamp("startDate").toLocalDateTime())); // Matches alias in SQL
            dto.setMaturityDate(String.valueOf(rs.getTimestamp("maturityDate").toLocalDateTime())); // Matches alias in SQL
            dto.setAmount(rs.getBigDecimal("amount")); // Matches alias in SQL
//            dto.setBusinessUnit(rs.getString("businessUnit")); // Matches alias in SQL
            dto.setFullRate(rs.getBigDecimal("fullRate")); // Matches alias in SQL
//            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
//            dto.setContractRate(rs.getBigDecimal("contractRate"));
//            dto.setDepositAmount(rs.getBigDecimal("depositAmount"));
            dto.setClrBalAmt(BigDecimal.ZERO);
            return dto;
        });
    }

    public List<AccountInfoDto> confirmDepositLiquidation(String accountNum){
        String sql = """
    SELECT DISTINCT
        foracid AS loanId,
        (SELECT foracid FROM tbaadm.gam WHERE acid = b.repayment_acid) AS acctNumber,
        acct_name AS customerName,
        b.open_effective_date AS openEffectiveDate,
        b.maturity_date AS maturityDate,
        deposit_amount AS depositAmount,
        (SELECT full_rate 
         FROM tbaadm.idt x 
         WHERE x.entity_id = c.entity_id 
         AND serial_num = (SELECT MAX(serial_num) FROM tbaadm.idt WHERE entity_id = x.entity_id) 
         AND ROWNUM = 1) AS contractRate,
        (SELECT free_code_1 FROM tbaadm.gac WHERE acid = b.repayment_acid) AS businessUnit,
        (SELECT ref_desc 
         FROM tbaadm.rct 
         WHERE ref_code = (
             SELECT free_code_2 
             FROM tbaadm.gac 
             WHERE acid = b.repayment_acid
         ) 
         AND ROWNUM = 1) AS relationshipOfficer
    FROM tbaadm.gam a
    JOIN tbaadm.tam b ON a.acid = b.acid
    JOIN tbaadm.idt c ON b.acid = c.entity_id
    WHERE acct_cls_flg = 'Y' 
      AND acct_cls_date IS NOT NULL 
      AND foracid = ?
    """;

        return jdbcTemplate.query(sql, new Object[]{accountNum}, (rs, rowNum) -> {
            AccountInfoDto dto = new AccountInfoDto();
            dto.setInvestmentId(rs.getString("loanId"));  // Matches alias in SQL
            dto.setAcctNumber(rs.getString("acctNumber"));  // Matches alias in SQL
            dto.setCustomerName(rs.getString("customerName"));  // Matches alias in SQL
            dto.setOpenEffectiveDate(rs.getTimestamp("openEffectiveDate").toLocalDateTime()); // Matches alias in SQL
            dto.setMaturityDate(rs.getTimestamp("maturityDate").toLocalDateTime()); // Matches alias in SQL
            dto.setDepositAmount(rs.getBigDecimal("depositAmount")); // Matches alias in SQL
            dto.setContractRate(rs.getBigDecimal("contractRate")); // Matches alias in SQL
            dto.setBusinessUnit(rs.getString("businessUnit")); // Matches alias in SQL
            dto.setRelationshipOfficer(rs.getString("relationshipOfficer")); // Matches alias in SQL
            return dto;
        });

    }

    public String closeDepositAccount(LocalRequest request) {
        // Prepare the JSON body using Jackson or any JSON library
        ObjectMapper mapper = new ObjectMapper();
        Fixml fixml = createFixmlRequest(request);
        String jsonRequest;
        log.info("url: {}", bankingUrl);
        try {
            jsonRequest = mapper.writeValueAsString(fixml);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert request to JSON", e);
        }

        // Build the request with JSON body and headers
        RequestBody body = RequestBody.create(jsonRequest, okhttp3.MediaType.parse(MediaType.APPLICATION_JSON.toString()));
        Request httpRequest = new Request.Builder()
                .url(bankingUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request using OkHttpClient
        try (Response response = createOkHttpClient().newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Log and return response body
            String responseBody = response.body().string();
            log.info(responseBody);
            return responseBody;
        } catch (IOException e) {
            log.error("Failed to close deposit account", e);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String addLoan(Fixml request){
        ObjectMapper mapper = new ObjectMapper();
        log.info("Request: {}", request);

        String jsonRequest;
        String xmlRequest;
        log.info("url: {}", addLoanUrl);
        try {
            xmlRequest = serializeToXml(request);
            log.info("xml request: {}", xmlRequest.getBytes(StandardCharsets.UTF_8));
            jsonRequest = mapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert request to JSON", e);
        }

        // Build the request with JSON body and headers
        RequestBody body = RequestBody.create(xmlRequest, okhttp3.MediaType.parse(MediaType.APPLICATION_JSON.toString()));
        Request httpRequest = new Request.Builder()
                .url(addLoanUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Content-Length", String.valueOf(jsonRequest.getBytes(StandardCharsets.UTF_8).length))
                .build();

        // Execute the request using OkHttpClient
        try (Response response = createOkHttpClient().newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Log and return response body
            String responseBody = response.body().string();
            log.info(responseBody);
            return responseBody;
        } catch (IOException e) {
            log.error("Failed to close deposit account", e);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Fixml createFixmlRequest(LocalRequest request){
        MessageKey messageKey = MessageKey.builder()
                .requestUUID("Req_" + UUID.randomUUID())
                .serviceRequestId("DepAcctClose")
                .serviceRequestVersion("10.2")
                .channelId("COR")
                .languageId("")
                .build();

        RequestMessageInfo messageInfo = RequestMessageInfo.builder()
                .bankId("BANK001")
                .timeZone("UTC")
                .messageDateTime(LocalDateTime.now().toString())
                .build();

        PasswordToken passwordToken = PasswordToken.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .build();

        Token token = Token.builder()
                .passwordToken(passwordToken)
                .build();

        Security security = Security.builder()
                .token(token)
                .build();

        RequestHeader requestHeader = RequestHeader.builder()
                .messageKey(messageKey)
                .requestMessageInfo(messageInfo)
                .security(security)
                .build();

        Header header = Header.builder()
                .requestHeader(requestHeader)
                .build();

        DepAcctId depAcctId = DepAcctId.builder()
                .acctId(request.getAcctId())
                .build();

        DepAcctCloseRq closeRq = DepAcctCloseRq.builder()
                .depAcctId(depAcctId)
                .closeModeFlg("N")
                .closeReasonCode("012")
                .build();

        DepositAcctCloseRequest closeRequest = DepositAcctCloseRequest.builder()
                .depAcctCloseRq(closeRq)
                .build();

        Body body = Body.builder()
                .depAcctCloseRequest(closeRequest)
                .build();

        return  Fixml.builder()
                .body(body)
                .header(header)
                .build();
    }






        public OkHttpClient createOkHttpClient() throws Exception {
            // Trust manager that accepts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509ExtendedTrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                        }
                    }
            };

            // Set up SSL context with custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an OkHttpClient.Builder and configure supported protocols
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509ExtendedTrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true);  // Trust all hostnames

            builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
            // Configure the client to support TLS 1.0, 1.1, 1.2, and 1.3
            builder.connectionSpecs(java.util.Arrays.asList(
                    new okhttp3.ConnectionSpec.Builder(okhttp3.ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_1, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0, TlsVersion.TLS_1_2, TlsVersion.TLS_1_3, TlsVersion.SSL_3_0)
                            .allEnabledCipherSuites()
                            .build(),
                    okhttp3.ConnectionSpec.CLEARTEXT));

            return builder.build();
        }

        public static String serializeToJson(Object object) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }

    public String serializeToXml(Object obj) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
    }






