package com.ken.infinity.services;

import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.PhotoRepository;
import com.ken.infinity.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoServiceImpl implements PhotoService {
    public PhotoRepository photoRepository;
    public UserRepository userRepository;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, UserRepository userRepository) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Photo photo, User user) {
        photo.setOwner(user);
        photo.setLikes(0);
        photo.setLabel("Verifying");
        // First save to obtain an ID
        Photo saved = photoRepository.save(photo);
        String finalImgUrl = "/img/artwork-photos/" + saved.getId() + "/" + saved.getImgUrl();
        saved.setImgUrl(finalImgUrl);
        photoRepository.save(saved);
    }

    @Override
    public List<Photo> getPhotos() {
        return photoRepository.findAll();
    }

    @Override
    public Photo findPhotoById(int id) {
        return photoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Photo> findPhotoByOwner(int id) {
        return photoRepository.findByOwnerId(id);
    }

    @Override
    public void updatePhoto(int id) {
        Photo photo = photoRepository.findById(id).orElse(null);
        if (photo != null) {
            photo.setLabel("Sold");
            photoRepository.save(photo);
        }
    }

    @Override
    public void updatePhotoLikes(int id, int likes) {
        Photo photo = photoRepository.findById(id).orElse(null);
        if (photo != null) {
            photo.setLikes(likes + 1);
            photoRepository.save(photo);
        }
    }

    @Override
    public String getPhotoOwnerName(Photo photo) {
        User user = photo.getOwner();
        if (user != null) {
            return user.getFirstName();
        }
        return "";
    }
}
