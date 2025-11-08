package com.ken.infinity.services;

import com.ken.infinity.models.Photoshoot;
import com.ken.infinity.models.PhotoshootRegister;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.PhotoshootRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoshootRegisterServiceImpl implements PhotoshootRegisterService {
    PhotoshootRegisterRepository photoshootRegisterRepository;

    @Autowired
    public PhotoshootRegisterServiceImpl(PhotoshootRegisterRepository photoshootRegisterRepository) {
        this.photoshootRegisterRepository = photoshootRegisterRepository;
    }

    @Override
    public void save(PhotoshootRegister photoshootRegister, User user, Photoshoot photoshoot) {
        photoshootRegister.setUser(user);
        photoshootRegister.setPhotoshoot(photoshoot);
        photoshootRegisterRepository.save(photoshootRegister);
    }
}
