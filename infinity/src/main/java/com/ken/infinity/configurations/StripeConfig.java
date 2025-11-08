package com.ken.infinity.configurations;

import com.stripe.Stripe;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe configuration initializer.
 * Sets the API key from environment variable STRIPE_API_KEY.
 * Docs: https://stripe.com/docs/api
 */
@Configuration
public class StripeConfig {

    @PostConstruct
    public void init() {
        String apiKey = System.getenv("STRIPE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[WARN] STRIPE_API_KEY not set. Stripe features will not work until provided.");
        } else {
            Stripe.apiKey = apiKey;
            System.out.println("[INFO] Stripe initialized.");
        }
    }
}
