package com.ken.infinity.repository;

import com.ken.infinity.models.Photo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    List<Photo> findByOwnerId(int ownerId);
    List<Photo> findByLabel(String label);
    List<Photo> findByCategory(String category);
}
