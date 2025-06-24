package com.hostelhive.hostelhive.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

@Service
public class MpesaService {

    @Value("${mpesa.consumer.key}")
    private String consumerKey;

    @Value("${mpesa.consumer.secret}")
    private String consumerSecret;

    @Value("${mpesa.shortcode}")
    private String shortCode;

    @Value("${mpesa.passkey}")
    private String passKey;

    @Value("${mpesa.callback.url}")
    private String callbackUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Generate access token for M-Pesa API
     * @return access token
     */
    public String getAccessToken() {
        String credentials = consumerKey + ":" + consumerSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials",
                HttpMethod.GET, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }

    /**
     * Generate password for STK Push
     * @return encoded password
     */
    private String generatePassword() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String strToEncode = shortCode + passKey + timestamp;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(strToEncode.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating password", e);
        }
    }

    /**
     * Initiate STK Push payment
     * @param phoneNumber student's phone number (e.g., 254712345678)
     * @param amount amount to pay
     * @param accountReference reference for the transaction
     * @param transactionDesc description of the transaction
     * @return STK Push response
     */
    public String initiateStkPush(String phoneNumber, double amount, String accountReference, String transactionDesc) {
        String accessToken = getAccessToken();
        String password = generatePassword();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("BusinessShortCode", shortCode);
        body.add("Password", password);
        body.add("Timestamp", timestamp);
        body.add("TransactionType", "CustomerPayBillOnline");
        body.add("Amount", String.valueOf(amount));
        body.add("PartyA", phoneNumber); // Should start with 254 for Kenya
        body.add("PartyB", shortCode);
        body.add("PhoneNumber", phoneNumber);
        body.add("CallBackURL", callbackUrl);
        body.add("AccountReference", accountReference);
        body.add("TransactionDesc", transactionDesc);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest",
                HttpMethod.POST, entity, Map.class);

        return response.getBody().toString();
    }

    /**
     * Initiate B2C disbursement to manager
     * @param phoneNumber manager's phone number
     * @param amount amount to disburse
     * @param remarks remarks for the transaction
     * @return B2C response
     */
    public String initiateB2CDisbursement(String phoneNumber, double amount, String remarks) {
        String accessToken = getAccessToken();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("Initiator", "hostelhive");
        body.add("SecurityCredential", "iKZAyoTIKbIHo4gpsTPCeqDR2kBx+3qDnKSuD6Mlkmk8pFmBW0NubGgOJvDYGWSTxakg99rSqAWj3L1Gr5pbqFNvts3qE2sX6z+nOROp+l0Uygkdc0ty1a3Sd6vuzRxMA0qOXPGkFH73bKvyAiL30Vsv3q2W7WxxQQjwtcVy+YEp+4Yzhip1VJvoD81ybHV5lpLd4+SToW9YB1+J7TgFV6OPuTcahL5qrl1WF9gj7GZSCkdQKWD/DgBNtI3tdvH/xOfU56kPMiO8XYOIeIMh380beDhwnm8gFCSNsjyH03KTcSd3fKGk7ojndFJNlicFSs3RHce9xX8Tap4VlAk0Zw==");
        body.add("CommandID", "SalaryPayment");
        body.add("Amount", String.valueOf(amount));
        body.add("PartyA", shortCode);
        body.add("PartyB", phoneNumber);
        body.add("Remarks", remarks);
        body.add("QueueTimeOutURL", callbackUrl);
        body.add("ResultURL", callbackUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest",
                HttpMethod.POST, entity, Map.class);

        return response.getBody().toString();
    }
}