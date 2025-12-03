package com.example.dacn2.repository.flight;
import com.example.dacn2.entity.flight.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {}
