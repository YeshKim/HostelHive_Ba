package com.hostelhive.hostelhive.Service;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MpesaService {

    @Value("${mpesa.api.url:https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest}")
    private String mpesaApiUrl;

    @Value("${mpesa.shortcode:174379}")
    private String shortcode;

    @Value("${mpesa.passkey:bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919}")
    private String passKey;

    @Value("${mpesa.consumer.key:TVAsWnu2XDw6S26pNtzhbxMtHpAZuc1flyCT5OtiL5GIUUYm}")
    private String consumerKey;

    @Value("${mpesa.consumer.secret:lgA0hTpzsmJnZFukGAps4YmxhTlIdymrs4YgVYAnZ2BwQYRfhAfgZBh8G97ECKkR}")
    private String consumerSecret;

    @Value("${mpesa.callback.url:https://<your-ngrok-subdomain>.ngrok.io/api/payments/handleCallback}")
    private String callbackUrl;

    public String initiateStkPush(String phoneNumber, double amount, String accountReference, String transactionDesc) {
        String requestPayload = createStkPushRequestPayload(phoneNumber, amount, accountReference, transactionDesc);
        String accessToken = "Bearer " + getAccessToken();

        if (accessToken == null || accessToken.equals("Bearer null")) {
            throw new RuntimeException("Failed to obtain M-PESA access token");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", accessToken);

        System.out.println("============>> Token ===========> " + accessToken);
        System.out.println("============>> Headers ===========> " + headers.toString());
        System.out.println("============>> Payload ===========> " + requestPayload);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            mpesaApiUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        String responseBody = response.getBody();
        System.out.println("============>> Response ===========> " + responseBody);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("M-PESA API request failed: " + responseBody);
        }

        return responseBody;
    }

    public String initiateB2CDisbursement(String phoneNumber, double amount, String remarks) {
        String b2cUrl = "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest";
        String requestPayload = createB2CRequestPayload(phoneNumber, amount, remarks);
        String accessToken = "Bearer " + getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            b2cUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        return response.getBody();
    }

    public Map<String, Object> queryTransactionStatus(String transactionId, String transactionType) {
        String queryUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query";
        String accessToken = "Bearer " + getAccessToken();
        Map<String, String> passwordData = generatePassword();
        String queryPayload = createQueryRequestPayload(transactionId, passwordData);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(queryPayload, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            queryUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return new JSONObject(response.getBody()).toMap();
        }
        return null;
    }

    private String createStkPushRequestPayload(String phoneNumber, double amount, String accountReference, String transactionDesc) {
        Map<String, String> password = generatePassword();
        return "{"
                + "\"BusinessShortCode\":\"" + shortcode.trim() + "\","
                + "\"Password\":\"" + password.get("password") + "\","
                + "\"Timestamp\":\"" + password.get("timestamp") + "\","
                + "\"TransactionType\":\"CustomerPayBillOnline\","
                + "\"Amount\":" + amount + ","
                + "\"PartyA\":\"" + phoneNumber + "\","
                + "\"PartyB\":\"" + shortcode.trim() + "\","
                + "\"PhoneNumber\":\"" + phoneNumber + "\","
                + "\"CallBackURL\":\"" + callbackUrl + "\","
                + "\"AccountReference\":\"" + accountReference + "\","
                + "\"TransactionDesc\":\"" + transactionDesc + "\""
                + "}";
    }

    private String createB2CRequestPayload(String phoneNumber, double amount, String remarks) {
        Map<String, String> password = generatePassword();
        return "{"
                + "\"InitiatorName\":\"testapi\","
                + "\"SecurityCredential\":\"" + password.get("password") + "\","
                + "\"CommandID\":\"BusinessPayment\","
                + "\"Amount\":" + amount + ","
                + "\"PartyA\":\"" + shortcode.trim() + "\","
                + "\"PartyB\":\"" + phoneNumber + "\","
                + "\"Remarks\":\"" + remarks + "\","
                + "\"QueueTimeOutURL\":\"" + callbackUrl + "/timeout\","
                + "\"ResultURL\":\"" + callbackUrl + "/result\""
                + "}";
    }

    private String createQueryRequestPayload(String transactionId, Map<String, String> passwordData) {
        return "{"
                + "\"BusinessShortCode\":\"" + shortcode.trim() + "\","
                + "\"Password\":\"" + passwordData.get("password") + "\","
                + "\"Timestamp\":\"" + passwordData.get("timestamp") + "\","
                + "\"CheckoutRequestID\":\"" + transactionId + "\""
                + "}";
    }

    public String getAccessToken() {
        String tokenUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(consumerKey, consumerSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = new RestTemplate().exchange(tokenUrl, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                return jsonResponse.getString("access_token");
            } else {
                System.err.println("Failed to get access token: " + response.getStatusCode() + " - " + response.getBody());
                throw new RuntimeException("Failed to get access token: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Exception while fetching access token: " + e.getMessage());
            throw new RuntimeException("Exception while fetching access token", e);
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public Map<String, String> generatePassword() {
        String timestamp = getCurrentTimestamp();
        String password = shortcode.trim() + passKey.trim() + timestamp;
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        return new HashMap<>(Map.of("timestamp", timestamp, "password", encodedPassword));
    }

    public String getShortcode() {
        return shortcode;
    }
}