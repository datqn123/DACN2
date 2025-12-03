package com.example.dacn2.repository.flight;

import com.example.dacn2.entity.flight.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {}
