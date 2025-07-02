package com.hostelhive.hostelhive.DTOs;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallbackMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("Item")
    private List<ItemDTO> items;

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}