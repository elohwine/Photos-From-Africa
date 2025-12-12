package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.OrderRepository;
import com.ken.infinity.services.PhotoService;
import com.ken.infinity.services.SecurityService;
import com.ken.infinity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {
    private final OrderRepository orderRepository;
    private final PhotoService photoService;
    private final SecurityService securityService;
    private final UserService userService;

    @Autowired
    public PaymentPageController(OrderRepository orderRepository, PhotoService photoService, SecurityService securityService, UserService userService) {
        this.orderRepository = orderRepository;
        this.photoService = photoService;
        this.securityService = securityService;
        this.userService = userService;
    }

    /**
     * Direct checkout: handles both logged-in users and guests.
     * - If user is logged in, we can associate with their account
     * - If guest, we proceed with email/address from form
     * We display the Stripe payment form without creating an order yet.
     */
    @GetMapping("/payments/checkout")
    public String checkoutGuest(@RequestParam("photoId") int photoId, @RequestParam("email") String email, @RequestParam("address") String address, Model model) {
        Photo photo = photoService.findPhotoById(photoId);
        if (photo == null) {
            model.addAttribute("error", "Photo not found");
            return "error";
        }

        // Check if user is logged in
        String username = securityService.findLoggedInUsername();
        User loggedInUser = null;
        if (username != null) {
            loggedInUser = userService.findByUsername(username);
        }

        java.math.BigDecimal bd = photo.getPrice();
        long amountInCents = bd == null ? 0L : bd.multiply(java.math.BigDecimal.valueOf(100)).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
        model.addAttribute("photoId", photo.getId());
        model.addAttribute("photoTitle", photo.getTitle());
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
        model.addAttribute("stripePk", System.getenv("STRIPE_PUBLISHABLE_KEY"));

        // Pass userId if logged in (for webhook to associate order properly)
        if (loggedInUser != null) {
            model.addAttribute("userId", loggedInUser.getId());
        }

        return "stripePay";
    }

    /**
     * Legacy endpoint for orders created before payment (authenticated flow).
     */
    @GetMapping("/payments/stripe")
    public String stripePay(@RequestParam("orderId") int orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "error";
        }
        java.math.BigDecimal bd2 = order.getPrice();
        long amountInCents = bd2 == null ? 0L : bd2.multiply(java.math.BigDecimal.valueOf(100)).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
        model.addAttribute("orderId", order.getId());
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
        model.addAttribute("stripePk", System.getenv("STRIPE_PUBLISHABLE_KEY"));
        return "stripePay";
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(@RequestParam(value = "orderId", required = false) Integer orderId, @RequestParam(value = "paymentIntentId", required = false) String paymentIntentId, Model model) {
        System.out.println("[DEBUG] /payments/success called with orderId=" + orderId + ", paymentIntentId=" + paymentIntentId);

        if (orderId != null) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                System.out.println("[ERROR] Order not found for orderId=" + orderId);
                model.addAttribute("error", "Order not found");
                return "error";
            }
            java.math.BigDecimal bd3 = order.getPrice();
            long amountInCents = bd3 == null ? 0L : bd3.multiply(java.math.BigDecimal.valueOf(100)).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
            model.addAttribute("orderId", order.getId());
            model.addAttribute("amount", amountInCents);
            model.addAttribute("currency", "usd");
            System.out.println("[SUCCESS] Showing payment success page for order " + orderId);
        } else if (paymentIntentId != null) {
            // Guest checkout: show generic success
            model.addAttribute("paymentIntentId", paymentIntentId);
            model.addAttribute("message", "Thank you for your purchase!");
            System.out.println("[SUCCESS] Showing guest payment success for paymentIntent=" + paymentIntentId);
        } else {
            System.out.println("[ERROR] No orderId or paymentIntentId provided");
            model.addAttribute("error", "Invalid payment reference");
            return "error";
        }
        return "paymentSuccess";
    }
}
