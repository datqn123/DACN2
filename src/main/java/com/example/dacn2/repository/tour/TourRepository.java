package com.example.dacn2.repository.tour;

import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.entity.tour.Tour;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    boolean existsBySlug(String slug); // Check trùng URL

    // đếm tổng tour
    @Query("SELECT COUNT(t) FROM Tour t")
    Long countTotalTours();

    @Query(value = "SELECT * FROM tours ORDER BY RAND()", nativeQuery = true)
    List<Tour> findFeaturedTours(Pageable pageable);

    /**
     * DTO Projection - Trả về trực tiếp TourCardResponse
     */
    @Query("SELECT new com.example.dacn2.dto.response.home.TourCardResponse(" +
            "t.id, t.title, t.slug, t.duration, " +
            "t.startLocation.name, t.destination.name, " +
            "t.thumbnail, t.price, t.transportation) " +
            "FROM Tour t ORDER BY RAND()")
    List<TourCardResponse> findFeaturedTourCards(Pageable pageable);
}
