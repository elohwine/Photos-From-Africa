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
        int currentUserId;
        currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
        User user = userService.findByUserId(currentUserId);
        System.out.println(user);
        System.out.println(photo_id);
        Photo photo = photoService.findPhotoById(photo_id);
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
        String to = user.getEmail();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Your order for Photo from Infinity Photo Gallery");
        message.setText("Hello " + user.getFirstName() + "! \n" + "Thanks for your order #" + order.getId() + " placed on " + order.getOrdered_at() + " with Infinity Photo Gallery." + " One of the best photos from our gallery is headed your way! \n" + "\n" + "Your Order total is " + order.getPrice() + "$. " + "We accept payment via cheque/debit/credit card. Simply reply to this mail to let us know how you wish to pay. We will send you a mail for further proceedings. " + "If you wish to cancel the order, let us know via replying to this mail. The due date for the payment is upto 15 days after recieving this mail. After that we may have to cancel your order. \n" + "\n" + "We love your choice of this master piece! If you have any queries, just reply to this mail and we'll be right back to you!" + "\n" + "\n" + "Sincerely, \n" + "Infinity Photo Gallery");

        javaMailSender.send(message);

        //        end sending mail

        // Redirect to Stripe payment page for immediate payment
        return "redirect:/payments/stripe?orderId=" + order.getId();
    }
}
