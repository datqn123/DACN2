package com.example.dacn2.dto.request;

import com.example.dacn2.entity.booking.BookingType;
import com.example.dacn2.entity.booking.PaymentMethod;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequest {
    // 1. Thông tin chung
    private BookingType type; // FLIGHT, HOTEL, TOUR
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private PaymentMethod paymentMethod;
    private String voucherCode; // Mã giảm giá (nếu có)
    private int quantity;

    // 2. Chi tiết dịch vụ (Tùy loại mà điền ID tương ứng)
    private Long roomId; // Nếu đặt KS
    private Long flightSeatId; // Nếu đặt Vé máy bay (ID hạng vé)
    private Long tourScheduleId; // Nếu đặt Tour

    // 3. Thời gian (Dành cho KS)
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // 4. Danh sách người đi
    // VD: [{name: "Nguyen Van A", type: "ADULT"}, {name: "Be B", type: "CHILD"}]
    private List<PassengerRequest> passengers;
}