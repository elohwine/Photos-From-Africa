package com.ken.infinity.repository;

import com.ken.infinity.models.Workshop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Integer> {
    List<Workshop> findByOrganizerId(int organizerId);
    List<Workshop> findByStatus(String status);
    List<Workshop> findByMode(String mode);
}
