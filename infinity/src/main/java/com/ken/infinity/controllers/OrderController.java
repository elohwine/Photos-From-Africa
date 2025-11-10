package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.services.OrderService;
import com.ken.infinity.services.PhotoService;
import com.ken.infinity.services.SecurityService;
import com.ken.infinity.services.UserService;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
    UserService userService;
    SecurityService securityService;
    OrderService orderService;
    PhotoService photoService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public OrderController(UserService userService, SecurityService securityService, OrderService orderService, PhotoService photoService) {
        this.userService = userService;
        this.securityService = securityService;
        this.orderService = orderService;
        this.photoService = photoService;
    }

    @PostMapping("/order")
    public String addOrder(@ModelAttribute("order") Order order, @RequestParam("photo_id") int photo_id, Model model) {
        model.addAttribute("loggedIn", securityService.isLoggedIn());
        // Handle both logged-in and guest checkout gracefully
        User user = null;
        String username = securityService.findLoggedInUsername();
        if (username != null) {
            user = userService.findByUsername(username);
        }

        if (user == null) {
            // Require authentication for ordering; redirect to login preserving desired photo
            return "redirect:/login?orderPhoto=" + photo_id;
        }
        System.out.println("Order user=" + user + ", photo_id=" + photo_id);
        Photo photo = photoService.findPhotoById(photo_id);
        if (photo == null) {
            System.out.println("Photo not found for id=" + photo_id);
            return "redirect:/shop?error=photo_not_found";
        }
        int price = photo.getPrice();
        order.setPrice(price);
        System.out.println(price);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        ts.setTime(1000 * (long) Math.floor(ts.getTime() / 1000));
        order.setOrdered_at(ts);
        System.out.println(ts);
        photoService.updatePhoto(photo_id);
    orderService.save(order, user, photo);

        //      start sending mail

    String from = "nairobi.sen.42@gmail.com";
    String to = (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) ? user.getEmail() : order.getEmail();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        if (to != null) {
            message.setTo(to);
        }
        message.setSubject("Your order from Photos For Africa");
    String greetingName = (user != null && user.getFirstName() != null && !user.getFirstName().isEmpty()) ? user.getFirstName() : to;
    message.setText("Hello " + greetingName + "! \n" +
        "Thanks for your order #" + order.getId() + " placed on " + order.getOrdered_at() + " with Photos For Africa. One of the best photos from our collection is headed your way! \n\n" +
        "Your Order total is " + order.getPrice() + "$. We accept payment via cheque/debit/credit card. Simply reply to this mail to let us know how you wish to pay. We will send you a mail for further proceedings. " +
        "If you wish to cancel the order, let us know via replying to this mail. The due date for the payment is up to 15 days after receiving this mail. After that we may have to cancel your order. \n\n" +
        "We love your choice of this masterpiece! If you have any queries, just reply to this mail and we'll be right back to you!\n\n" +
        "Sincerely, \nPhotos For Africa");

        javaMailSender.send(message);

        //        end sending mail

        // Redirect to Stripe payment page for immediate payment
        return "redirect:/payments/stripe?orderId=" + order.getId();
    }
}
