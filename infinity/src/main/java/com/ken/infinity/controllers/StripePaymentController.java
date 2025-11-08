package com.ken.infinity.controllers;

import com.ken.infinity.dto.CreatePaymentRequest;
import com.ken.infinity.dto.CreatePaymentResponse;
import com.ken.infinity.services.StripePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class StripePaymentController {
    private final StripePaymentService stripePaymentService;

    @Autowired
    public StripePaymentController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePaymentRequest request, @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws Exception {
        return stripePaymentService.createPaymentIntent(request, idempotencyKey);
    }
}
