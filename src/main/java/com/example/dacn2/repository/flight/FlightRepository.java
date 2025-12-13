package com.example.dacn2.repository.flight;

import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.entity.flight.FlightSeat;

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

    // filter: điểm đi, điểm đến, ngày bay, khoảng giá, hãng hàng không
    @Query("SELECT DISTINCT f FROM Flight f " +
            "LEFT JOIN f.flightSeats fs " +
            "WHERE (:fromLocId IS NULL OR f.departureAirport.location.id = :fromLocId) " +
            "AND (:toLocId IS NULL OR f.arrivalAirport.location.id = :toLocId) " +
            "AND (:startDate IS NULL OR f.departureTime >= :startDate) " +
            "AND (:endDate IS NULL OR f.departureTime < :endDate) " +
            "AND (:minPrice IS NULL OR fs.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR fs.price <= :maxPrice) " +
            "AND (:airlineIds IS NULL OR f.airline.id IN :airlineIds) " +
            "ORDER BY f.departureTime ASC")
    List<Flight> searchFlights(
            Long fromLocId,
            Long toLocId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Double minPrice,
            Double maxPrice,
            List<Long> airlineIds);

    // 3. Tìm chuyến bay sắp khởi hành (cho trang chủ - Flight Card)
    // Lấy các chuyến bay có departureTime >= thời điểm hiện tại, sắp xếp theo thời
    // gian gần nhất
    @Query("SELECT f FROM Flight f WHERE f.departureTime >= :now ORDER BY f.departureTime ASC")
    List<Flight> findUpcomingFlights(LocalDateTime now, Pageable pageable);

    // lấy các hạng ghế
    @Query("SELECT fs FROM FlightSeat fs WHERE fs.flight.id = :flightId")
    List<FlightSeat> getSeatClasses(Long flightId);
}
