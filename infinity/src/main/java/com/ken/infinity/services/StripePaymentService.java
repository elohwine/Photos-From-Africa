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
        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(req.getAmount())
                .setCurrency(req.getCurrency())
                .addPaymentMethodType("card");

        // Add metadata from request (can include orderId or photoId, email, address)
        if (req.getOrderId() != null && !req.getOrderId().isBlank()) {
            builder.putMetadata("orderId", req.getOrderId());
            builder.setDescription("Payment for order " + req.getOrderId());
        }
        if (req.getMetadata() != null) {
            req.getMetadata().forEach(builder::putMetadata);
            if (req.getMetadata().containsKey("photoId")) {
                builder.setDescription("Payment for photo " + req.getMetadata().get("photoId"));
            }
        }

        PaymentIntentCreateParams params = builder.build();
        RequestOptions requestOptions = idempotencyKey != null && !idempotencyKey.isBlank() 
                ? RequestOptions.builder().setIdempotencyKey(idempotencyKey).build() 
                : RequestOptions.getDefault();
        PaymentIntent intent = PaymentIntent.create(params, requestOptions);
        return new CreatePaymentResponse(intent.getId(), intent.getClientSecret());
    }
}
