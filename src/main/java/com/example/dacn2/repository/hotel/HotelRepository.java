package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.Hotel;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByNameAndAddress(String name, String address);

    @Query("SELECT h FROM Hotel h WHERE h.starRating >= 3 ORDER BY h.starRating DESC")
    List<Hotel> findFeaturedHotels(Pageable pageable);
}
