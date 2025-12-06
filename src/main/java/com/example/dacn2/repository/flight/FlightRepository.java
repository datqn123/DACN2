package com.example.dacn2.repository.flight;

import com.example.dacn2.entity.flight.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Tìm ra nhưng chuyến bay có giá rẻ nhất
    @Query("SELECT f FROM Flight f JOIN f.flightSeats fs WHERE fs.availableQuantity > 0 ORDER BY fs.price ASC")
    List<Flight> findTopDeals(Pageable pageable);

    // 2. Tìm kiếm chuyến bay (Cho trang Search)
    // Tìm theo: Nơi đi, Nơi đến, Ngày đi
    @Query("SELECT f FROM Flight f " +
            "WHERE f.departureAirport.location.id = :fromLocId " +
            "AND f.arrivalAirport.location.id = :toLocId " +
            "AND f.departureTime >= :startDate " +
            "AND f.departureTime < :endDate")
    List<Flight> searchFlights(Long fromLocId, Long toLocId, LocalDateTime startDate, LocalDateTime endDate);
}
