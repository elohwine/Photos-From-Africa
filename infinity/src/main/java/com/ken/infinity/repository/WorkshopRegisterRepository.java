package com.ken.infinity.repository;

import com.ken.infinity.models.WorkshopRegister;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopRegisterRepository extends JpaRepository<WorkshopRegister, Integer> {
    List<WorkshopRegister> findByUserId(int userId);
    List<WorkshopRegister> findByWorkshopId(int workshopId);
}
