package com.hostelhive.hostelhive.DTOs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}