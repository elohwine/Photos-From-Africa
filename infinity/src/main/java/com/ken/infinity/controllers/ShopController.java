package com.ken.infinity.controllers;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.repository.PhotoRepository;
import com.ken.infinity.services.OrderService;
import com.ken.infinity.services.PhotoService;
import com.ken.infinity.services.SecurityService;
import com.ken.infinity.services.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ShopController {
    UserService userService;
    PhotoRepository photoRepository;
    PhotoService photoService;
    SecurityService securityService;
    OrderService orderService;

    @Autowired
    public ShopController(UserService userService, PhotoRepository photoRepository, PhotoService photoService, SecurityService securityService, OrderService orderService) {
        this.userService = userService;
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.securityService = securityService;
        this.orderService = orderService;
    }

    @GetMapping({ "/shop" })
    public String shop(Model model) {
        List<Photo> photos = photoRepository.findAll();

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();
        for (Photo photo : photos) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In controller : " + photos);

        model.addAttribute("photos", photos);
        model.addAttribute("photoAndOwner", photoAndOwner);
        System.out.println(model);

        return "shop";
    }

    @GetMapping("/hatching")
    public String hatching(Model model) {
        List<Photo> photos = photoRepository.findByCategory("hatching");

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();
        for (Photo photo : photos) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In controller : " + photos);

        model.addAttribute("photos", photos);
        model.addAttribute("photoAndOwner", photoAndOwner);
        System.out.println(model);

        return "hatching";
    }

    @GetMapping("/watercolorPainting")
    public String watercolorPainting(Model model) {
        List<Photo> photos = photoRepository.findByCategory("watercolor");

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();
        for (Photo photo : photos) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In controller : " + photos);

        model.addAttribute("photos", photos);
        model.addAttribute("photoAndOwner", photoAndOwner);
        System.out.println(model);

        return "watercolorPainting";
    }

    @GetMapping("/oilPainting")
    public String oilPainting(Model model) {
        List<Photo> photos = photoRepository.findByCategory("oil");

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();
        for (Photo photo : photos) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In controller : " + photos);

        model.addAttribute("photos", photos);
        model.addAttribute("photoAndOwner", photoAndOwner);
        System.out.println(model);

        return "oilPainting";
    }

    @GetMapping("/receivedPhotos")
    public String receivedPhotos(Model model) {
        List<Photo> photos = photoRepository.findByLabel("Verifying");

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();
        for (Photo photo : photos) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In controller : " + photos);

        model.addAttribute("photos", photos);
        model.addAttribute("photoAndOwner", photoAndOwner);
        System.out.println(model);

        return "receivedPhotos";
    }

    @RequestMapping(value = "/acceptPhoto", method = { RequestMethod.GET, RequestMethod.POST })
    public String acceptPhoto(Model model, @RequestParam("photo_id") int photo_id) {
        Photo photo = photoRepository.findById(photo_id).orElse(null);
        if (photo != null) {
            photo.setLabel("Unsold");
            photoRepository.save(photo);
        }
        return "redirect:/receivedPhotos";
    }

    @RequestMapping(value = "/declinePhoto", method = { RequestMethod.GET, RequestMethod.POST })
    public String declinePhoto(Model model, @RequestParam("photo_id") int photo_id) {
        photoRepository.deleteById(photo_id);
        return "redirect:/receivedPhotos";
    }

    @RequestMapping(value = "/shop/{photoId}", method = { RequestMethod.GET, RequestMethod.POST })
    public String PhotoById(Model model, @PathVariable("photoId") int photoId) {
        model.addAttribute("loggedIn", securityService.isLoggedIn());
        Map<String, Object> map = new HashMap<String, Object>();
        Order order = new Order();
        model.addAttribute(order);
        Photo photo = photoService.findPhotoById(photoId);
        map.put("photo", photo);
        model.addAttribute("owner", photoService.getPhotoOwnerName(photo));
        model.addAllAttributes(map);

        photoService.updatePhotoLikes(photoId, photo.getLikes());

        if (securityService.isLoggedIn()) return "singlePhoto"; else return "login";
    }
}
