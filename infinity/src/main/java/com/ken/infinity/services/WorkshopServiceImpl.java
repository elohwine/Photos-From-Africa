package com.ken.infinity.services;

import com.ken.infinity.models.User;
import com.ken.infinity.models.Workshop;
import com.ken.infinity.repository.UserRepository;
import com.ken.infinity.repository.WorkshopRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkshopServiceImpl implements WorkshopService {
    WorkshopRepository workshopRepository;
    UserRepository userRepository;

    @Autowired
    public WorkshopServiceImpl(WorkshopRepository workshopRepository, UserRepository userRepository) {
        this.workshopRepository = workshopRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Workshop> getWorkshops() {
        return workshopRepository.findAll();
    }

    @Override
    public void save(Workshop workshop, User user) {
        workshop.setOrganizer(user);
        workshop.setRegistered_seats(0);
        String imgUrl = "/img/workshop-photos/" + workshop.getId() + "/" + workshop.getImgUrl();
        workshop.setImgUrl(imgUrl);
        workshop.setStatus("notDone");
        workshopRepository.save(workshop);
    }

    @Override
    public Workshop findWorkshopById(int id) {
        return workshopRepository.findById(id).orElse(null);
    }

    @Override
    public List<Workshop> findWorkshopByOrganizer(int id) {
        return workshopRepository.findByOrganizerId(id);
    }

    @Override
    public void updateWorkshopSeats(int id, int seats) {
        Workshop workshop = workshopRepository.findById(id).orElse(null);
        if (workshop != null) {
            workshop.setRegistered_seats(seats);
            workshopRepository.save(workshop);
        }
    }

    @Override
    public void updateWorkshopStatus(int id) {
        Workshop workshop = workshopRepository.findById(id).orElse(null);
        if (workshop != null) {
            workshop.setStatus("Done");
            workshopRepository.save(workshop);
        }
    }

    @Override
    public String getWorkshopOrganizerName(Workshop workshop) {
        User user = workshop.getOrganizer();
        if (user != null) {
            return user.getFirstName();
        }
        return "";
    }
}
