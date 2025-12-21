package com.example.dacn2.repository.hotel;

import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.entity.hotel.Hotel;

import java.util.List;

import org.springframework.data.domain.Page;
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

        // đếm tổng hotel
        @Query("SELECT COUNT(h) FROM Hotel h")
        Long countTotalHotels();

        @Query("SELECT new com.example.dacn2.dto.response.home.HotelCardResponse(" +
                        "h.id, h.name, h.address, h.starRating, h.totalReviews, " +
                        "h.location.name, " +
                        "(SELECT img.imageUrl FROM HotelImage img WHERE img.hotel.id = h.id ORDER BY img.id ASC LIMIT 1), "
                        +
                        "h.pricePerNightFrom, h.type) " +
                        "FROM Hotel h " +
                        "WHERE (:locationId IS NULL OR h.location.id = :locationId OR h.location.parent.id = :locationId) "
                        +
                        "AND (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                        "AND (:minStarRating IS NULL OR h.starRating >= :minStarRating) " +
                        "AND (:hotelType IS NULL OR h.type = :hotelType) " +
                        "AND (:minPrice IS NULL OR h.pricePerNightFrom >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR h.pricePerNightFrom <= :maxPrice)")
        Page<HotelCardResponse> findHotelCardsWithFilters(
                        @Param("locationId") Long locationId,
                        @Param("name") String name,
                        @Param("minStarRating") Integer minStarRating,
                        @Param("hotelType") com.example.dacn2.entity.hotel.HotelType hotelType,
                        @Param("minPrice") Double minPrice,
                        @Param("maxPrice") Double maxPrice,
                        Pageable pageable);

        @Query("SELECT new com.example.dacn2.dto.response.home.HotelCardResponse(h.id, h.name, h.address, h.starRating, h.totalReviews, h.location.name, (SELECT img.imageUrl FROM HotelImage img WHERE img.hotel.id = h.id ORDER BY img.id ASC LIMIT 1), h.pricePerNightFrom, h.type) FROM Hotel h WHERE h.location.id = :locationId")
        List<HotelCardResponse> findByLocationId(@Param("locationId") Long locationId);

        @Query("SELECT new com.example.dacn2.dto.response.home.HotelCardResponse(h.id, h.name, h.address, h.starRating, h.totalReviews, h.location.name, (SELECT img.imageUrl FROM HotelImage img WHERE img.hotel.id = h.id ORDER BY img.id ASC LIMIT 1), h.pricePerNightFrom, h.type) FROM Hotel h")
        Page<HotelCardResponse> findAllHotelCards(Pageable pageable);
}
