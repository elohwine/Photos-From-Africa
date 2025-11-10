package com.ken.infinity.controllers;

import com.ken.infinity.models.Photo;
import com.ken.infinity.models.Workshop;
import com.ken.infinity.repository.PhotoRepository;
import com.ken.infinity.services.PhotoService;
import com.ken.infinity.services.WorkshopService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomepageController {
    PhotoRepository photoRepository;
    PhotoService photoService;
    WorkshopService workshopService;

    @Autowired
    public HomepageController(PhotoRepository photoRepository, PhotoService photoService, WorkshopService workshopService) {
        this.photoRepository = photoRepository;
        this.photoService = photoService;
        this.workshopService = workshopService;
    }

    @RequestMapping({ "/", "/homepage" })
    public String homepage(Model model) {
        List<Photo> photos = photoRepository.findAll();
        List<Photo> featured = photos.subList(Math.max(0, photos.size() - 6), photos.size());

        Map<Object, String> photoAndOwner = new HashMap<Object, String>();

        for (Photo photo : featured) {
            photoAndOwner.put(photo, photoService.getPhotoOwnerName(photo));
        }

        System.out.println("In home controller : " + featured);

        // Homepage template expects "artworks" and "artAndOwner" attributes
        model.addAttribute("artworks", featured);
        model.addAttribute("artAndOwner", photoAndOwner);
        System.out.println(model);

        List<Workshop> workshops = workshopService.getWorkshops();
        Map<Object, String> workshopAndOrganizer = new HashMap<>();
        for (Workshop workshop : workshops) {
            workshopAndOrganizer.put(workshop, workshopService.getWorkshopOrganizerName(workshop));
        }
        model.addAttribute("workshops", workshops);
        model.addAttribute("workshopAndOrganizer", workshopAndOrganizer);

        return "homepage";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
