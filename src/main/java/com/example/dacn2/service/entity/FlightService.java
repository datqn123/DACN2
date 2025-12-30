package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.flight.FlightRequest;
import com.example.dacn2.dto.request.flight.FlightSeatRequest;
import com.example.dacn2.dto.response.FlightSeatResponse;
import com.example.dacn2.dto.response.FligthCardResponse;
import com.example.dacn2.entity.flight.*;
import com.example.dacn2.repository.flight.*;
import com.example.dacn2.service.user_service.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirlineRepository airlineRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private FileUploadService fileUploadService;

    // 1. Lấy tất cả
    public List<Flight> getAll() {
        return flightRepository.findAll();
    }

    // 2. Lấy chi tiết
    public Flight getById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay ID: " + id));
    }

    // 3. Tạo mới
    @Transactional
    public Flight create(FlightRequest request, MultipartFile imageFile) {
        Flight flight = new Flight();

        // Xử lý ảnh
        String imageUrl = processImage(request.getImage(), imageFile);
        flight.setImage(imageUrl);

        mapRequestToEntity(request, flight);
        return flightRepository.save(flight);
    }

    // 4. Cập nhật (Hàm bạn cần)
    @Transactional
    public Flight update(Long id, FlightRequest request, MultipartFile imageFile) {
        Flight flight = getById(id);

        // Xử lý ảnh (Nếu có ảnh mới thì ghi đè, không thì giữ nguyên ảnh cũ)
        String imageUrl = processImage(request.getImage(), imageFile);
        if (imageUrl != null) {
            flight.setImage(imageUrl);
        }

        mapRequestToEntity(request, flight);
        return flightRepository.save(flight);
    }

    // 5. Xóa
    @Transactional
    public void delete(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Chuyến bay không tồn tại");
        }
        flightRepository.deleteById(id);
    }

    // Hiển thị card chuyến bay - Lấy các chuyến bay sắp khởi hành (từ thời điểm
    // hiện tại)
    public List<Flight> getFlightsForCard(int limit) {
        return flightRepository.findUpcomingFlights(
                java.time.LocalDateTime.now(),
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    // Lấy danh sách FlightCardResponse cho hiển thị
    public List<FligthCardResponse> getFlightCardsForDisplay(int limit) {
        List<Flight> flights = getFlightsForCard(limit);
        return flights.stream()
                .map(this::convertToCardResponse)
                .toList();
    }

    // tìm kiếm chuyến bay theo bộ lọc (điểm đi, điểm đến, ngày bay, khoảng giá,
    // hãng bay)
    public List<FligthCardResponse> searchFlightsForDisplay(
            Long departureLocationId,
            Long arrivalLocationId,
            java.time.LocalDate departureDate,
            Double minPrice,
            Double maxPrice,
            java.util.List<Long> airlineIds) {

        java.time.LocalDateTime startOfDay = departureDate != null ? departureDate.atStartOfDay() : null;
        java.time.LocalDateTime endOfDay = departureDate != null ? departureDate.plusDays(1).atStartOfDay() : null;

        List<Flight> flights = flightRepository.searchFlights(
                departureLocationId,
                arrivalLocationId,
                startOfDay,
                endOfDay,
                minPrice,
                maxPrice,
                airlineIds);

        return flights.stream()
                .map(this::convertToCardResponse)
                .toList();
    }

    public List<FlightSeatResponse> getSeatClasses(Long flightId) {
        return flightRepository.getSeatClasses(flightId).stream()
                .map(this::convertToSeatResponse)
                .toList();
    }

    private FlightSeatResponse convertToSeatResponse(FlightSeat flightSeat) {
        return FlightSeatResponse.builder()
                .seatClass(flightSeat.getSeatClass())
                .price(flightSeat.getPrice())
                .availableQuantity(flightSeat.getAvailableQuantity())
                .cabinBaggage(flightSeat.getCabinBaggage())
                .checkedBaggage(flightSeat.getCheckedBaggage())
                .isRefundable(flightSeat.getIsRefundable())
                .isChangeable(flightSeat.getIsChangeable())
                .hasMeal(flightSeat.getHasMeal())
                .build();
    }

    private FligthCardResponse convertToCardResponse(Flight flight) {
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Double lowestPrice = flight.getFlightSeats() != null
                ? flight.getFlightSeats().stream()
                        .filter(seat -> seat.getAvailableQuantity() != null && seat.getAvailableQuantity() > 0)
                        .mapToDouble(FlightSeat::getPrice)
                        .min()
                        .orElse(0.0)
                : 0.0;

        return FligthCardResponse.builder()
                .id(flight.getId())

                .airlineName(flight.getAirline() != null ? flight.getAirline().getName() : null)
                .airlineLogo(flight.getAirline() != null ? flight.getAirline().getLogoUrl() : null)

                .departureCode(flight.getDepartureAirport() != null ? flight.getDepartureAirport().getCode() : null)
                .departureCity(
                        flight.getDepartureAirport() != null && flight.getDepartureAirport().getLocation() != null
                                ? flight.getDepartureAirport().getLocation().getName()
                                : null)

                .arrivalCode(flight.getArrivalAirport() != null ? flight.getArrivalAirport().getCode() : null)
                .arrivalCity(flight.getArrivalAirport() != null && flight.getArrivalAirport().getLocation() != null
                        ? flight.getArrivalAirport().getLocation().getName()
                        : null)

                .departureTime(
                        flight.getDepartureTime() != null ? flight.getDepartureTime().format(timeFormatter) : null)
                .departureTime(
                        flight.getDepartureTime() != null ? flight.getDepartureTime().format(timeFormatter) : null)
                .arrivalTime(flight.getArrivalTime() != null ? flight.getArrivalTime().format(timeFormatter) : null)
                .duration(flight.getDuration())
                .flightDate(flight.getDepartureTime() != null ? flight.getDepartureTime().format(dateFormatter) : null)

                .originalPrice(lowestPrice)

                .flightNumber(flight.getFlightNumber())
                .build();
    }

    // --- HÀM PHỤ TRỢ: Map dữ liệu từ Request sang Entity ---
    private void mapRequestToEntity(FlightRequest request, Flight flight) {
        // A. Thông tin cơ bản
        flight.setFlightNumber(request.getFlightNumber());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        // Tự động tính thời gian bay
        if (request.getDepartureTime() != null && request.getArrivalTime() != null) {
            long minutes = Duration.between(request.getDepartureTime(), request.getArrivalTime()).toMinutes();
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            flight.setDuration(hours + "h " + remainingMinutes + "m");
        }

        // B. Gán Hãng bay
        if (request.getAirlineId() != null) {
            Airline airline = airlineRepository.findById(request.getAirlineId())
                    .orElseThrow(() -> new RuntimeException("Hãng bay không tồn tại"));
            flight.setAirline(airline);
        }

        // C. Gán Sân bay
        if (request.getDepartureAirportId() != null) {
            Airport depAirport = airportRepository.findById(request.getDepartureAirportId())
                    .orElseThrow(() -> new RuntimeException("Sân bay đi không tồn tại"));
            flight.setDepartureAirport(depAirport);
        }

        if (request.getArrivalAirportId() != null) {
            Airport arrAirport = airportRepository.findById(request.getArrivalAirportId())
                    .orElseThrow(() -> new RuntimeException("Sân bay đến không tồn tại"));
            flight.setArrivalAirport(arrAirport);
        }

        // D. Xử lý Hạng ghế (Flight Seats) - Logic: Xóa cũ thay mới
        if (request.getSeats() != null) {
            List<FlightSeat> newSeats = new ArrayList<>();

            for (FlightSeatRequest seatReq : request.getSeats()) {
                FlightSeat seat = new FlightSeat();
                seat.setSeatClass(seatReq.getSeatClass());
                seat.setPrice(seatReq.getPrice());
                seat.setAvailableQuantity(seatReq.getQuantity());

                // Map quyền lợi
                seat.setCabinBaggage(seatReq.getCabinBaggage());
                seat.setCheckedBaggage(seatReq.getCheckedBaggage());
                seat.setIsRefundable(seatReq.getIsRefundable());
                seat.setIsChangeable(seatReq.getIsChangeable());
                seat.setHasMeal(seatReq.getHasMeal());

                seat.setFlight(flight); // Gán quan hệ ngược lại
                newSeats.add(seat);
            }

            // Cập nhật vào danh sách của Flight
            if (flight.getFlightSeats() == null) {
                flight.setFlightSeats(newSeats);
            } else {
                flight.getFlightSeats().clear(); // Xóa ghế cũ
                flight.getFlightSeats().addAll(newSeats); // Thêm ghế mới
            }
        }
    }

    private String processImage(String linkUrl, MultipartFile file) {
        try {
            // Ưu tiên 1: Upload file nếu có
            if (file != null && !file.isEmpty()) {
                return fileUploadService.uploadFile(file);
            }
            // Ưu tiên 2: Dùng link gửi kèm
            if (linkUrl != null && !linkUrl.isEmpty()) {
                return linkUrl;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload ảnh chuyến bay: " + e.getMessage());
        }
    }
}