package com.ken.infinity.controllers;

import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.UserRepository;
import com.ken.infinity.services.PhotoService;
import com.ken.infinity.services.SecurityService;
import com.ken.infinity.services.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AddPhotoController {
    public PhotoService photoService;
    public UserService userService;
    public SecurityService securityService;

    @Autowired
    public AddPhotoController(PhotoService photoService, UserService userService, SecurityService securityService) {
        this.photoService = photoService;
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/addPhoto")
    public String addPhoto(Model model) {
        model.addAttribute("photo", new Photo());
        return "addPhoto";
    }

    @PostMapping("/addPhoto")
    public String addPhoto(@ModelAttribute("photo") Photo photo, Model model, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        model.addAttribute("loggedIn", securityService.isLoggedIn());
        int currentUserId;
        try {
            currentUserId = userService.findByUsername(securityService.findLoggedInUsername()).getId();
        } catch (Exception e) {
            return "redirect:/login";
        }

        User user = userService.findByUserId(currentUserId);
        System.out.println("current user id" + currentUserId);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        photo.setImgUrl(fileName);
        photoService.save(photo, user);

        String uploadDir = "src/main/resources/static/img/photo-photos/" + photo.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return "redirect:/homepage";
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
