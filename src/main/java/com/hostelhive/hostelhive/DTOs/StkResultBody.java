package com.hostelhive.hostelhive.DTOs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkResultBody implements Serializable {
    private static final long serialVersionUID = 2439593685316345235L;

    @JsonProperty("Body")
    private StkBody body;

    public StkResultBody() {
        super();
    }

    public StkResultBody(StkBody body) {
        super();
        this.body = body;
    }

    public StkBody getBody() {
        return body;
    }

    public void setBody(StkBody body) {
        this.body = body;
    }
}