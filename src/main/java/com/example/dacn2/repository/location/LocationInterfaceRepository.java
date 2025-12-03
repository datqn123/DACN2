package com.example.dacn2.repository.location;

import com.example.dacn2.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationInterfaceRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    Optional<Location> findBySlug(String slug);

    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    @Query("SELECT l FROM Location l WHERE l.type IN ('COUNTRY', 'PROVINCE') ORDER BY RAND() LIMIT 4")
    List<Location> findFeaturedLocations();
}