package com.ken.infinity.services;

import com.ken.infinity.models.Photoshoot;
import com.ken.infinity.repository.PhotoshootRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoshootServiceImpl implements PhotoshootService {
    PhotoshootRepository photoshootRepository;

    @Autowired
    public PhotoshootServiceImpl(PhotoshootRepository photoshootRepository) {
        this.photoshootRepository = photoshootRepository;
    }

    @Override
    public List<Photoshoot> getPhotoshoots() {
        return photoshootRepository.findAll();
    }

    @Override
    public void save(Photoshoot photoshoot) {
        photoshoot.setRegistered_seats(0);
        String imgUrl = "/img/photoshoot-photos/" + photoshoot.getId() + "/" + photoshoot.getImgUrl();
        photoshoot.setImgUrl(imgUrl);
        photoshoot.setStatus("notDone");
        photoshootRepository.save(photoshoot);
    }

    @Override
    public Photoshoot findPhotoshootById(int id) {
        return photoshootRepository.findById(id).orElse(null);
    }

    @Override
    public void updatePhotoshootSeats(int id, int seats) {
        Photoshoot photoshoot = photoshootRepository.findById(id).orElse(null);
        if (photoshoot != null) {
            photoshoot.setRegistered_seats(seats);
            photoshootRepository.save(photoshoot);
        }
    }

    @Override
    public void updatePhotoshootStatus(int id) {
        Photoshoot photoshoot = photoshootRepository.findById(id).orElse(null);
        if (photoshoot != null) {
            photoshoot.setStatus("Done");
            photoshootRepository.save(photoshoot);
        }
    }
}
