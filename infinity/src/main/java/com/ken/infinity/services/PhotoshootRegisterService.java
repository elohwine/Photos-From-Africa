package com.ken.infinity.services;

import com.ken.infinity.models.*;

public interface PhotoshootRegisterService {
    void save(PhotoshootRegister photoshootRegister, User user, Photoshoot photoshoot);
}
