package com.example.dacn2.repository.location;

import com.example.dacn2.dto.response.DropdownLocationResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.entity.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationInterfaceRepository extends JpaRepository<Location, Long> {
        Optional<Location> findByName(String name);

        Optional<Location> findBySlug(String slug);

        boolean existsBySlug(String slug);

        boolean existsByName(String name);

        @Query("SELECT l FROM Location l WHERE l.type IN ('COUNTRY', 'PROVINCE') ORDER BY RAND() LIMIT 10")
        List<Location> findFeaturedLocations();

        /**
         * DTO Projection - Trả về trực tiếp LocationCardResponse
         */
        @Query("SELECT new com.example.dacn2.dto.response.home.LocationCardResponse(" +
                        "l.id, l.name, l.slug, l.thumbnail) " +
                        "FROM Location l WHERE l.type IN ('COUNTRY', 'PROVINCE') ORDER BY RAND() LIMIT 10")
        List<com.example.dacn2.dto.response.home.LocationCardResponse> findFeaturedLocationCards();

        @Query("SELECT new com.example.dacn2.dto.response.home.LocationSearchResult(" +
                        "l.id, l.name, p.name, l.type, COUNT(h.id)) " + // Lấy p.name thay vì l.parent.name
                        "FROM Location l " +
                        "LEFT JOIN l.parent p " + // <--- THÊM DÒNG NÀY (Để lấy cả Quốc gia không có cha)
                        "LEFT JOIN l.hotels h " +
                        "WHERE l.type IN ('PROVINCE', 'ISLAND', 'COUNTRY') " + // Đảm bảo có COUNTRY
                        "GROUP BY l.id, l.name, p.name, l.type " + // Group theo p.name
                        "ORDER BY COUNT(h.id) DESC")
        List<LocationSearchResult> findTopDestinations(Pageable top10);

        @Query("SELECT new com.example.dacn2.dto.response.home.LocationSearchResult(" +
                        "l.id, l.name, p.name, l.type, COUNT(h.id)) " +
                        "FROM Location l " +
                        "LEFT JOIN l.parent p " + // <--- THÊM LEFT JOIN
                        "JOIN l.hotels h " +
                        "WHERE l.type IN ('PROVINCE', 'ISLAND', 'COUNTRY') " +
                        "AND (:keyword IS NULL OR l.name LIKE %:keyword%) " +
                        "GROUP BY l.id, l.name, p.name, l.type " +
                        "ORDER BY COUNT(h.id) DESC")
        List<LocationSearchResult> searchLocationsWithHotelCount(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT l FROM Location l WHERE l.parent.slug = :slugCountry")
        List<Location> findChildLocationByParentSlug(@Param("slugCountry") String slugCountry);

        @Query("SELECT l FROM Location l WHERE l.type = 'COUNTRY'")
        List<Location> getCountryToHotelPage();

        @Query("SELECT l FROM Location l WHERE l.type IN ('PROVINCE')")
        List<Location> getFeaturedLocationsToHotelPage(Pageable pageable);

        // Dropdown cho search flight
        @Query("SELECT new com.example.dacn2.dto.response.DropdownLocationResponse(l.id, l.name) FROM Location l")
        List<DropdownLocationResponse> getDropdownLocations();

        @Query("SELECT l FROM Location l where l.parent.slug = 'viet-nam'")
        List<Location> getVnLocation();
}