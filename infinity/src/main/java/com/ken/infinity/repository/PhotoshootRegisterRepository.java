package com.ken.infinity.repository;

import com.ken.infinity.models.PhotoshootRegister;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoshootRegisterRepository extends JpaRepository<PhotoshootRegister, Integer> {
    List<PhotoshootRegister> findByUserId(int userId);
    List<PhotoshootRegister> findByPhotoshootId(int photoshootId);
}
