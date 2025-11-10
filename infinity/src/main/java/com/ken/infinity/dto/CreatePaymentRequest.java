package com.ken.infinity.dto;

import java.util.Map;

public class CreatePaymentRequest {
    private Long amount; // in smallest currency unit (e.g., cents)
    private String currency;
    private String orderId; // optional: for authenticated flow
    private Map<String, String> metadata; // for guest checkout: photoId, email, address

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
