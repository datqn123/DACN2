package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.Hotel;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {
    boolean existsByNameAndAddress(String name, String address);

    @Query("SELECT h FROM Hotel h WHERE h.starRating >= 3 ORDER BY h.starRating DESC")
    List<Hotel> findFeaturedHotels(Pageable pageable);

    // Tìm hotels theo slug của location
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.location.slug) = LOWER(:locationSlug)")
    List<Hotel> findByLocationSlug(@Param("locationSlug") String locationSlug);
}
