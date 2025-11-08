package com.ken.infinity.services;

import com.ken.infinity.models.Photoshoot;
import com.ken.infinity.models.User;
import com.ken.infinity.models.Workshop;
import java.util.List;

public interface PhotoshootService {
    List<Photoshoot> getPhotoshoots();
    void save(Photoshoot photoshoot);
    Photoshoot findPhotoshootById(int id);
    void updatePhotoshootSeats(int id, int seats);
    void updatePhotoshootStatus(int id);
}
