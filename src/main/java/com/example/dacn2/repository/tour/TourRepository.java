package com.example.dacn2.repository.tour;

import com.example.dacn2.entity.tour.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    boolean existsBySlug(String slug); // Check trùng URL
}
