package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.OrderRepository;
import com.ken.infinity.repository.UserRepository;
import com.ken.infinity.services.PhotoService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import java.util.Map;
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
    private final UserRepository userRepository;
    private final PhotoService photoService;

    @Autowired
    public StripeWebhookController(OrderRepository orderRepository, JavaMailSender javaMailSender, UserRepository userRepository, PhotoService photoService) {
        this.orderRepository = orderRepository;
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.photoService = photoService;
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
                Map<String, String> metadata = pi.getMetadata();

                String orderIdStr = metadata != null ? metadata.get("orderId") : null;
                String photoIdStr = metadata != null ? metadata.get("photoId") : null;

                if (orderIdStr != null) {
                    // Existing authenticated flow: order created before payment
                    handleExistingOrder(pi, type, orderIdStr);
                } else if (photoIdStr != null) {
                    // Guest checkout: create order now after successful payment
                    if ("payment_intent.succeeded".equals(type)) {
                        handleGuestCheckout(pi, metadata);
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
        return ResponseEntity.ok("received");
    }

    private void handleExistingOrder(PaymentIntent pi, String type, String orderIdStr) {
        try {
            Integer orderId = Integer.parseInt(orderIdStr);
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setPaymentProvider("stripe");
                order.setExternalPaymentId(pi.getId());
                if ("payment_intent.succeeded".equals(type)) {
                    order.setPaymentStatus("SUCCEEDED");
                    order.setStatus("Paid");
                    sendPaymentConfirmationEmail(order);
                } else if ("payment_intent.payment_failed".equals(type)) {
                    order.setPaymentStatus("FAILED");
                }
                orderRepository.save(order);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void handleGuestCheckout(PaymentIntent pi, Map<String, String> metadata) {
        try {
            Integer photoId = Integer.parseInt(metadata.get("photoId"));
            String email = metadata.get("email");
            String address = metadata.get("address");
            String userIdStr = metadata.get("userId");
            Long amountCents = pi.getAmount();
            int priceUsd = (int) (amountCents / 100);

            // Find photo and user if applicable
            Photo photo = photoService.findPhotoById(photoId);
            User user = null;

            // Priority 1: userId from metadata (logged-in user at checkout)
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    Integer userId = Integer.parseInt(userIdStr);
                    user = userRepository.findById(userId).orElse(null);
                } catch (NumberFormatException ignored) {}
            }

            // Priority 2: Check if email belongs to existing user
            if (user == null && email != null && !email.isEmpty()) {
                user = userRepository.findByEmail(email);
            }

            // Create order record
            Order order = new Order();
            order.setEmail(email);
            order.setAddress(address);
            order.setPrice(priceUsd);
            order.setStatus("Paid");
            order.setOrdered_at(new java.sql.Timestamp(System.currentTimeMillis()));
            order.setPaymentProvider("stripe");
            order.setExternalPaymentId(pi.getId());
            order.setPaymentStatus("SUCCEEDED");

            // Associate with user if found
            if (user != null) {
                order.setUser(user);
            }

            // Associate with photo if found
            if (photo != null) {
                order.setPhoto(photo);
                // Update photo status to sold
                photoService.updatePhoto(photoId);
            }

            orderRepository.save(order);

            // Send confirmation email
            if (user != null) {
                sendPaymentConfirmationEmail(order);
            } else {
                sendGuestConfirmationEmail(order, email);
            }
        } catch (Exception e) {
            System.err.println("Error handling guest checkout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendGuestConfirmationEmail(Order order, String toEmail) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Cannot send email: recipient email is null or empty");
            return;
        }
        try {
            String from = "nairobi.sen.42@gmail.com";
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(toEmail);
            message.setSubject("Payment Confirmation - Photos For Africa");
            message.setText("Dear Customer,\n\n" + "Your payment has been successfully processed!\n" + "Order ID: " + order.getId() + "\n" + "Amount Paid: $" + order.getPrice() + "\n" + "Payment ID: " + order.getExternalPaymentId() + "\n\n" + "Your photo will be delivered to: " + order.getAddress() + "\n\n" + "Thank you for choosing Photos For Africa!\n\n" + "Best regards,\nPhotos For Africa");
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send guest confirmation email: " + e.getMessage());
        }
    }

    private void sendPaymentConfirmationEmail(Order order) {
        User user = order.getUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            String from = "nairobi.sen.42@gmail.com";
            String to = user.getEmail();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Payment Confirmation - Photos For Africa");
            String firstName = (user.getFirstName() != null && !user.getFirstName().isEmpty()) ? user.getFirstName() : "Customer";
            message.setText("Dear " + firstName + ",\n\n" + "Your payment for Order #" + order.getId() + " has been successfully processed.\n" + "Amount Paid: $" + order.getPrice() + "\n" + "Payment ID: " + order.getExternalPaymentId() + "\n\n" + "Your photos will be delivered shortly. Thank you for choosing Photos For Africa!\n\n" + "Best regards,\nPhotos For Africa");

            javaMailSender.send(message);
        }
    }
}
