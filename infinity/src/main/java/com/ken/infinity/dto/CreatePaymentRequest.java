package com.ken.infinity.dto;

public class CreatePaymentRequest {
    private Long amount; // in smallest currency unit (e.g., cents)
    private String currency;
    private String orderId;

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
}
