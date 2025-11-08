package com.ken.infinity.services;

import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import java.util.List;

public interface PhotoService {
    List<Photo> getPhotos();
    void save(Photo photo, User user);
    Photo findPhotoById(int id);
    List<Photo> findPhotoByOwner(int id);
    void updatePhoto(int id);
    void updatePhotoLikes(int id, int likes);
    String getPhotoOwnerName(Photo photo);
}
