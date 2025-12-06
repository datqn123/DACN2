package com.example.dacn2.repository.tour;

import com.example.dacn2.entity.tour.Tour;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    boolean existsBySlug(String slug); // Check trùng URL

    @Query(value = "SELECT * FROM tours ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Tour> findFeaturedTours();
}
