package com.example.dacn2.repository.flight;

import com.example.dacn2.entity.flight.FlightSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightSeatRepository extends JpaRepository<FlightSeat, Long> {}
