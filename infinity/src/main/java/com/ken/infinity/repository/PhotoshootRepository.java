package com.ken.infinity.repository;

import com.ken.infinity.models.Photoshoot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoshootRepository extends JpaRepository<Photoshoot, Integer> {
    List<Photoshoot> findByStatus(String status);
    List<Photoshoot> findByMode(String mode);
}
