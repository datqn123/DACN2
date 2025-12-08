package com.example.dacn2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dacn2.entity.booking.BookingPassenger;

@Repository
public interface PassengerRepository extends JpaRepository<BookingPassenger, Long> {
}
