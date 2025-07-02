package com.hostelhive.hostelhive.DTOs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkBody implements Serializable {
    private static final long serialVersionUID = -8299792939453467688L;

    @JsonProperty("stkCallback")
    private StkCallback stkCallback;

    public StkBody() {
        super();
    }

    public StkBody(StkCallback stkCallback) {
        super();
        this.stkCallback = stkCallback;
    }

    public StkCallback getStkCallback() {
        return stkCallback;
    }

    public void setStkCallback(StkCallback stkCallback) {
        this.stkCallback = stkCallback;
    }
}