package com.ken.infinity.services;

import com.ken.infinity.models.User;
import com.ken.infinity.models.Workshop;
import com.ken.infinity.models.WorkshopRegister;
import com.ken.infinity.repository.WorkshopRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkshopRegisterServiceImpl implements WorkshopRegisterService {
    WorkshopRegisterRepository workshopRegisterRepository;

    @Autowired
    public WorkshopRegisterServiceImpl(WorkshopRegisterRepository workshopRegisterRepository) {
        this.workshopRegisterRepository = workshopRegisterRepository;
    }

    @Override
    public void save(WorkshopRegister workshopRegister, User user, Workshop workshop) {
        workshopRegister.setUser(user);
        workshopRegister.setWorkshop(workshop);
        workshopRegisterRepository.save(workshopRegister);
    }
}
