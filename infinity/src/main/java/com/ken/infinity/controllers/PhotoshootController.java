package com.ken.infinity.controllers;

import com.ken.infinity.models.*;
import com.ken.infinity.services.PhotoshootRegisterService;
import com.ken.infinity.services.PhotoshootService;
import com.ken.infinity.services.SecurityService;
import com.ken.infinity.services.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PhotoshootController {
    @Autowired
    JavaMailSender javaMailSender;

    PhotoshootService photoshootService;
    SecurityService securityService;
    UserService userService;
    PhotoshootRegisterService photoshootRegisterService;

    @Autowired
    public PhotoshootController(PhotoshootService photoshootService, SecurityService securityService, UserService userService, PhotoshootRegisterService photoshootRegisterService) {
        this.photoshootService = photoshootService;
        this.securityService = securityService;
        this.userService = userService;
        this.photoshootRegisterService = photoshootRegisterService;
    }

    @GetMapping("/photoshoot")
    public String photoshoot(Model model) {
        List<Photoshoot> photoshoots = photoshootService.getPhotoshoots();
        model.addAttribute("photoshoots", photoshoots);

        return "photoshoot";
    }

    @GetMapping("/heldPhotoshoot")
    public String heldPhotoshoot(Model model) {
        model.addAttribute("photoshoot", new Photoshoot());
        return "heldPhotoshoot";
    }

    @PostMapping("/heldPhotoshoot")
    public String heldPhotoshoot(@ModelAttribute("photoshoot") Photoshoot photoshoot, Model model, @RequestParam("image") MultipartFile multipartFile, @RequestParam("localDatetime") String localDatetime) throws IOException {
        model.addAttribute("loggedIn", securityService.isLoggedIn());
        int currentUserId;
        try {
            currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
        } catch (Exception e) {
            return "redirect:/login";
        }

        String originalName = multipartFile.getOriginalFilename();
        String fileName = StringUtils.cleanPath(originalName != null ? originalName : ("photoshoot-" + System.currentTimeMillis()));
        photoshoot.setImgUrl(fileName);

        System.out.println("Current datetime " + localDatetime);
        String datetimeTimestamp = localDatetime;
        datetimeTimestamp += ":00";

        System.out.println(datetimeTimestamp);

        photoshoot.setDatetime(Timestamp.valueOf(datetimeTimestamp.replace("T", " ")));

        photoshootService.save(photoshoot);

        String uploadDir = "src/main/resources/static/img/photoshoot-photos/" + photoshoot.getId();

        PhotoshootController.FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return "redirect:/photoshoot";
    }

    @GetMapping("/singlePhotoshoot")
    public String singlePhotoshoot() {
        return "singlePhotoshoot";
    }

    @RequestMapping("/photoshoot/{photoshootId}")
    public String PhotoshootById(Model model, @PathVariable("photoshootId") int photoshootId) {
        model.addAttribute("loggedIn", securityService.isLoggedIn());
        Map<String, Object> map = new HashMap<String, Object>();

        Photoshoot photoshoot = photoshootService.findPhotoshootById(photoshootId);

        map.put("photoshoot", photoshoot);

        PhotoshootRegister photoshootRegister = new PhotoshootRegister();
        model.addAttribute(photoshootRegister);

        model.addAllAttributes(map);

        if (securityService.isLoggedIn()) return "singlePhotoshoot";
        return "redirect:/login";
    }

    @RequestMapping(value = "/confirmPhotoshoot", method = { RequestMethod.GET, RequestMethod.POST })
    public String RegisterPhotoshoot(@ModelAttribute("photoshootRegister") PhotoshootRegister photoshootRegister, @RequestParam("photoshoot_id") int photoshoot_id, Model model) {
        model.addAttribute("loggedIn", securityService.isLoggedIn());

        int currentUserId;
        try {
            currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
        } catch (Exception e) {
            return "redirect:/login";
        }

        User user = userService.findByUserId(currentUserId);
        System.out.println(user);
        System.out.println(photoshoot_id);
        Photoshoot photoshoot = photoshootService.findPhotoshootById(photoshoot_id);
        photoshootService.updatePhotoshootSeats(photoshoot_id, photoshoot.getRegistered_seats());

        photoshootRegisterService.save(photoshootRegister, user, photoshoot);

        //      start sending mail

        String from = "noreply@infinity.example";
        String to = user.getEmail() != null ? user.getEmail() : "noreply@infinity.example";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Seat Registered for Photoshoot");
        message.setText("Hello " + user.getFirstName() + "! \n" + "You have successfully registered for the Photoshoot. " + "Hope to see you soon and have an amazing experience!" + "\n" + "\n" + "Sincerely, \n" + "Photos For Africa");

        javaMailSender.send(message);

        //        end sending mail

        return "redirect:/photoshoot";
    }

    private static class FileUploadUtil {

        public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }
    }
}
