package com.hostelhive.hostelhive.DTOs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkCallback implements Serializable {
    private static final long serialVersionUID = -5000969152577019399L;

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;
    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;
    @JsonProperty("ResultCode")
    private int resultCode;
    @JsonProperty("ResultDesc")
    private String resultDesc;

    @JsonProperty("CallbackMetadata")
    private CallbackMetadata callbackMetadata;

    public StkCallback() {
        super();
    }

    public StkCallback(String merchantRequestID, String checkoutRequestID, int resultCode, String resultDesc,
            CallbackMetadata callbackMetadata) {
        super();
        this.merchantRequestID = merchantRequestID;
        this.checkoutRequestID = checkoutRequestID;
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
        this.callbackMetadata = callbackMetadata;
    }

    public String getMerchantRequestID() {
        return merchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        this.merchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return checkoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        this.checkoutRequestID = checkoutRequestID;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public CallbackMetadata getCallbackMetadata() {
        return callbackMetadata;
    }

    public void setCallbackMetadata(CallbackMetadata callbackMetadata) {
        this.callbackMetadata = callbackMetadata;
    }
}