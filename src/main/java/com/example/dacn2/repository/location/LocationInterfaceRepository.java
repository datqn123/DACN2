package com.example.dacn2.repository.location;

import com.example.dacn2.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationInterfaceRepository extends JpaRepository<Location, Long> {
    // Tìm địa điểm theo slug (đường dẫn thân thiện)
    Optional<Location> findBySlug(String slug);

    // Kiểm tra xem slug đã tồn tại chưa (tránh trùng lặp)
    boolean existsBySlug(String slug);

    // check by name
    boolean existsByName(String name);
}