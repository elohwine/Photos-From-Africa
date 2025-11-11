package com.ken.infinity.services;

import com.ken.infinity.dto.CreatePaymentRequest;
import com.ken.infinity.dto.CreatePaymentResponse;
import com.ken.infinity.models.Photo;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service wrapping Stripe PaymentIntent creation.
 * Docs: https://stripe.com/docs/payments/payment-intents
 */
@Service
public class StripePaymentService {
    private final PhotoService photoService;

    @Autowired
    public StripePaymentService(PhotoService photoService) {
        this.photoService = photoService;
    }

    public CreatePaymentResponse createPaymentIntent(CreatePaymentRequest req, String idempotencyKey) throws Exception {
        // Derive amount/currency securely if photoId is present (guest checkout)
        Long amount = req.getAmount();
        String currency = req.getCurrency();
        Map<String, String> md = req.getMetadata();
        if (md != null && md.containsKey("photoId")) {
            try {
                int photoId = Integer.parseInt(md.get("photoId"));
                Photo p = photoService.findPhotoById(photoId);
                if (p != null) {
                    amount = (long) p.getPrice() * 100L; // cents
                    // Default all payments to USD for now
                    currency = (currency == null || currency.isBlank()) ? "usd" : currency;
                }
            } catch (NumberFormatException ignore) {}
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid amount for payment intent");
        }
        if (currency == null || currency.isBlank()) {
            currency = "usd";
        }

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder().setAmount(amount).setCurrency(currency).addPaymentMethodType("card");

        // Add metadata from request (can include orderId or photoId, email, address)
        if (req.getOrderId() != null && !req.getOrderId().isBlank()) {
            builder.putMetadata("orderId", req.getOrderId());
            builder.setDescription("Payment for order " + req.getOrderId());
        }
        if (md != null) {
            md.forEach(builder::putMetadata);
            if (md.containsKey("photoId")) {
                builder.setDescription("Payment for photo " + md.get("photoId"));
            }
        }

        PaymentIntentCreateParams params = builder.build();
        RequestOptions requestOptions = idempotencyKey != null && !idempotencyKey.isBlank() ? RequestOptions.builder().setIdempotencyKey(idempotencyKey).build() : RequestOptions.getDefault();
        PaymentIntent intent = PaymentIntent.create(params, requestOptions);
        return new CreatePaymentResponse(intent.getId(), intent.getClientSecret());
    }
}
