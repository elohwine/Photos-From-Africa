package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentPageController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/payments/stripe")
    public String stripePay(@RequestParam("orderId") int orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "error";
        }
        long amountInCents = ((long) order.getPrice()) * 100L; // price is in whole units
        model.addAttribute("orderId", order.getId());
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
        return "stripePay";
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(@RequestParam("orderId") int orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "error";
        }
        long amountInCents = ((long) order.getPrice()) * 100L;
        model.addAttribute("orderId", order.getId());
        model.addAttribute("amount", amountInCents);
        model.addAttribute("currency", "usd");
        return "paymentSuccess";
    }
}
