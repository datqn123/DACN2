package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByNameAndAddress(String name, String address);
}
