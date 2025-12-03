package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.Amenity;
import com.example.dacn2.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
