package com.ken.infinity.dto;

public class CreatePaymentResponse {
    private String paymentIntentId;
    private String clientSecret;

    public CreatePaymentResponse(String paymentIntentId, String clientSecret) {
        this.paymentIntentId = paymentIntentId;
        this.clientSecret = clientSecret;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
