package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.OrderRepository;
import com.ken.infinity.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserProfileController {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public UserProfileController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/userProfile")
    public String userProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        if (user != null) {
            List<Order> orders = orderRepository.findByUserId(user.getId());
            model.addAttribute("user", user);
            model.addAttribute("orders", orders);
        }
        return "userProfile";
    }
}
