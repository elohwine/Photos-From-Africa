package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.OrderRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {
    private final OrderRepository orderRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public StripeWebhookController(OrderRepository orderRepository, JavaMailSender javaMailSender) {
        this.orderRepository = orderRepository;
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/stripe")
    @Transactional
    public ResponseEntity<String> handleWebhook(@RequestHeader("Stripe-Signature") String sigHeader, @RequestBody String payload) {
        String secret = System.getenv("STRIPE_WEBHOOK_SECRET");
        if (secret == null || secret.isBlank()) {
            return ResponseEntity.status(500).body("Webhook secret not configured");
        }
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, secret);
            String type = event.getType();

            Optional<Object> maybeObj = event.getDataObjectDeserializer().getObject().map(o -> (Object) o);
            if (maybeObj.isPresent() && maybeObj.get() instanceof PaymentIntent) {
                PaymentIntent pi = (PaymentIntent) maybeObj.get();
                String orderIdStr = pi.getMetadata() != null ? pi.getMetadata().get("orderId") : null;
                Integer orderId = null;
                try {
                    if (orderIdStr != null) orderId = Integer.parseInt(orderIdStr);
                } catch (NumberFormatException ignored) {}

                if (orderId != null) {
                    Order order = orderRepository.findById(orderId).orElse(null);
                    if (order != null) {
                        order.setPaymentProvider("stripe");
                        order.setExternalPaymentId(pi.getId());
                        if ("payment_intent.succeeded".equals(type)) {
                            order.setPaymentStatus("SUCCEEDED");
                            order.setStatus("Paid");
                            // Send payment confirmation email
                            sendPaymentConfirmationEmail(order);
                        } else if ("payment_intent.payment_failed".equals(type)) {
                            order.setPaymentStatus("FAILED");
                        }
                        orderRepository.save(order);
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
        return ResponseEntity.ok("received");
    }

    private void sendPaymentConfirmationEmail(Order order) {
        User user = order.getUser();
        if (user != null && user.getEmail() != null) {
            String from = "nairobi.sen.42@gmail.com";
            String to = user.getEmail();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Payment Confirmation - Infinity Photo Gallery");
            message.setText("Dear " + user.getFirstName() + ",\n\n" + "Your payment for Order #" + order.getId() + " has been successfully processed.\n" + "Amount Paid: $" + order.getPrice() + "\n" + "Payment ID: " + order.getExternalPaymentId() + "\n\n" + "Your photos will be delivered shortly. Thank you for choosing Infinity Photo Gallery!\n\n" + "Best regards,\nInfinity Photo Gallery");

            javaMailSender.send(message);
        }
    }
}
