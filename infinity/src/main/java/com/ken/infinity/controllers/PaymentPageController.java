package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.repository.OrderRepository;
import com.ken.infinity.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {
    private final OrderRepository orderRepository;
    private final PhotoService photoService;

    @Autowired
    public PaymentPageController(OrderRepository orderRepository, PhotoService photoService) {
        this.orderRepository = orderRepository;
        this.photoService = photoService;
    }

    /**
     * Direct checkout: guest user provides email, address, and photo ID.
     * We display the Stripe payment form without creating an order yet.
     */
    @GetMapping("/payments/checkout")
    public String checkoutGuest(
            @RequestParam("photoId") int photoId,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            Model model) {
        Photo photo = photoService.findPhotoById(photoId);
        if (photo == null) {
            model.addAttribute("error", "Photo not found");
            return "error";
        }
        long amountInCents = ((long) photo.getPrice()) * 100L;
        model.addAttribute("photoId", photo.getId());
        model.addAttribute("photoTitle", photo.getTitle());
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
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
        long amountInCents = ((long) order.getPrice()) * 100L;
        model.addAttribute("orderId", order.getId());
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
        return "stripePay";
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(
            @RequestParam(value = "orderId", required = false) Integer orderId,
            @RequestParam(value = "paymentIntentId", required = false) String paymentIntentId,
            Model model) {
        if (orderId != null) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                model.addAttribute("error", "Order not found");
                return "error";
            }
            long amountInCents = ((long) order.getPrice()) * 100L;
            model.addAttribute("orderId", order.getId());
            model.addAttribute("amount", amountInCents);
            model.addAttribute("currency", "usd");
        } else if (paymentIntentId != null) {
            // Guest checkout: show generic success
            model.addAttribute("paymentIntentId", paymentIntentId);
            model.addAttribute("message", "Thank you for your purchase!");
        } else {
            model.addAttribute("error", "Invalid payment reference");
            return "error";
        }
        return "paymentSuccess";
    }
}
