package com.ken.infinity.services;

import com.ken.infinity.dto.CreatePaymentRequest;
import com.ken.infinity.dto.CreatePaymentResponse;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

/**
 * Service wrapping Stripe PaymentIntent creation.
 * Docs: https://stripe.com/docs/payments/payment-intents
 */
@Service
public class StripePaymentService {

    public CreatePaymentResponse createPaymentIntent(CreatePaymentRequest req, String idempotencyKey) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(req.getAmount()).setCurrency(req.getCurrency()).addPaymentMethodType("card").setDescription("Payment for order " + req.getOrderId()).putMetadata("orderId", req.getOrderId()).build();

        RequestOptions requestOptions = idempotencyKey != null && !idempotencyKey.isBlank() ? RequestOptions.builder().setIdempotencyKey(idempotencyKey).build() : RequestOptions.getDefault();
        PaymentIntent intent = PaymentIntent.create(params, requestOptions);
        return new CreatePaymentResponse(intent.getId(), intent.getClientSecret());
    }
}
