package com.example.dacn2.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.booking.BookingStatus;
import com.example.dacn2.entity.booking.BookingType;
import com.example.dacn2.entity.booking.PaymentMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private BookingType type;
    private BookingStatus status;
    private LocalDateTime createdAt;

    // Thông tin liên hệ
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // Thông tin dịch vụ (Rút gọn)
    private ServiceInfo service;

    // Thời gian (Hotel)
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    // Thanh toán
    private Integer quantity;
    private Double totalPrice;
    private Double discountAmount;
    private Double finalPrice;
    private PaymentMethod paymentMethod;
    private Boolean isPaid;

    // Voucher (Rút gọn)
    private VoucherInfo voucher;

    // Passengers (Rút gọn - chỉ tên)
    private List<PassengerInfo> passengers;

    // ========== Nested DTOs ==========
    @Data
    @Builder
    public static class ServiceInfo {
        private Long id;
        private String name;
        private String type; // "ROOM", "FLIGHT_SEAT", "TOUR_SCHEDULE"
        private Double price;
        // Thêm field tùy loại
        private String hotelName; // Nếu là Room
        private String flightCode; // Nếu là Flight
        private String tourName; // Nếu là Tour
    }

    @Data
    @Builder
    public static class VoucherInfo {
        private String code;
        private String name;
        private Double discountValue;
    }

    @Data
    @Builder
    public static class PassengerInfo {
        private String fullName;
        private String passengerType;
    }

    // ========== Converter ==========
    public static BookingResponse fromEntity(Booking booking) {
        BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .type(booking.getType())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .contactName(booking.getContactName())
                .contactPhone(booking.getContactPhone())
                .contactEmail(booking.getContactEmail())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .quantity(booking.getQuantity())
                .totalPrice(booking.getTotalPrice())
                .discountAmount(booking.getDiscountAmount())
                .finalPrice(booking.getFinalPrice())
                .paymentMethod(booking.getPaymentMethod())
                .isPaid(booking.getIsPaid());

        // Service Info
        if (booking.getRoom() != null) {
            builder.service(ServiceInfo.builder()
                    .id(booking.getRoom().getId())
                    .name(booking.getRoom().getName())
                    .type("ROOM")
                    .price(booking.getRoom().getPrice())
                    .hotelName(booking.getRoom().getHotel() != null
                            ? booking.getRoom().getHotel().getName()
                            : null)
                    .build());
        } else if (booking.getFlightSeat() != null) {
            builder.service(ServiceInfo.builder()
                    .id(booking.getFlightSeat().getId())
                    .name(booking.getFlightSeat().getSeatClass().toString())
                    .type("FLIGHT_SEAT")
                    .price(booking.getFlightSeat().getPrice())
                    .flightCode(booking.getFlight() != null
                            ? booking.getFlight().getFlightNumber()
                            : null)
                    .build());
        } else if (booking.getTourSchedule() != null) {
            builder.service(ServiceInfo.builder()
                    .id(booking.getTourSchedule().getId())
                    .name(booking.getTourSchedule().getTour().getTitle())
                    .type("TOUR_SCHEDULE")
                    .tourName(booking.getTour() != null
                            ? booking.getTour().getTitle()
                            : null)
                    .build());
        }

        // Voucher Info
        if (booking.getVoucher() != null) {
            builder.voucher(VoucherInfo.builder()
                    .code(booking.getVoucher().getCode())
                    .name(booking.getVoucher().getName())
                    .discountValue(booking.getVoucher().getDiscountValue())
                    .build());
        }

        // Passengers Info
        if (booking.getPassengers() != null && !booking.getPassengers().isEmpty()) {
            builder.passengers(booking.getPassengers().stream()
                    .map(p -> PassengerInfo.builder()
                            .fullName(p.getFullName())
                            .passengerType(p.getPassengerType())
                            .build())
                    .toList());
        }

        return builder.build();
    }
}
