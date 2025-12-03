package com.example.dacn2.repository.tour;

import com.example.dacn2.entity.tour.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long> {}
