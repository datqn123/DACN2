package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.flight.FlightRequest;
import com.example.dacn2.dto.request.flight.FlightSeatRequest;
import com.example.dacn2.entity.flight.*;
import com.example.dacn2.repository.flight.*;
import com.example.dacn2.service.user_service.FileUploadService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
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