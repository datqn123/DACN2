package com.example.dacn2.entity.booking;

import com.example.dacn2.entity.booking.BookingStatus;
import com.example.dacn2.entity.booking.BookingType;
import com.example.dacn2.entity.booking.PaymentMethod;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.entity.flight.FlightSeat;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.entity.tour.TourSchedule;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.User.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_user", columnList = "user_id"),
        @Index(name = "idx_booking_room", columnList = "room_id"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_checkin", columnList = "checkInDate"),
        @Index(name = "idx_booking_checkout", columnList = "checkOutDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String bookingCode; // Mã đơn hàng (VD: TGO-123456) để khách tra cứu

    // --- 1. THÔNG TIN NGƯỜI ĐẶT ---
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user; // Tài khoản người đặt

    private String contactName; // Tên người liên hệ (có thể khác tên tk)
    private String contactPhone; // SĐT liên hệ
    private String contactEmail; // Email nhận vé

    // --- 2. THÔNG TIN DỊCH VỤ (Polymorphic) ---
    @Enumerated(EnumType.STRING)
    private BookingType type; // HOTEL, FLIGHT, TOUR

    // -> Nếu là Đặt Phòng Khách Sạn
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    // -> Nếu là Đặt Vé Máy Bay
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
    @ManyToOne
    @JoinColumn(name = "flight_seat_id")
    private FlightSeat flightSeat; // Hạng vé nào (Eco/Business)

    // -> Nếu là Đặt Tour
    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;
    @ManyToOne
    @JoinColumn(name = "tour_schedule_id")
    private TourSchedule tourSchedule; // Đi chuyến ngày nào

    // --- 3. THÔNG TIN KHÁCH HÀNG ĐI CÙNG (Passenger) ---
    // (Lưu danh sách tên người đi máy bay / người ở khách sạn)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingPassenger> passengers;

    // --- 4. THANH TOÁN & GIÁ ---
    private Integer quantity; // Số lượng (Số phòng / Số vé / Số người)

    private Double totalPrice; // Tổng tiền gốc
    private Double discountAmount; // Tiền giảm giá (Voucher)
    private Double finalPrice; // Tiền phải thanh toán (Total - Discount)

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // PENDING, CONFIRMED...

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Boolean isPaid = false; // Đã trả tiền chưa?

    // Voucher đã dùng (để tracking)
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}