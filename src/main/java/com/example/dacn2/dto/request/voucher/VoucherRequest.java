package com.example.dacn2.dto.request.voucher;

import com.example.dacn2.entity.voucher.DiscountType;
import com.example.dacn2.entity.voucher.VoucherScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VoucherRequest {
    @NotBlank(message = "Mã voucher không được để trống")
    private String code; // TRIP30

    @NotBlank(message = "Tên voucher không được để trống")
    private String name;

    private String description;

    // Link ảnh (nếu copy paste)
    private String imageUrl;

    @NotNull
    private DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT

    @NotNull
    private Double discountValue; // 30 hoặc 50000

    private Double maxDiscountAmount; // Giảm tối đa bao nhiêu?
    private Double minOrderValue; // Đơn tối thiểu

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer usageLimit; // Tổng số lượng
    private Integer userLimit; // Giới hạn mỗi người

    @NotNull
    private VoucherScope scope; // GLOBAL, HOTEL_ONLY...

    // --- DANH SÁCH ÁP DỤNG ---
    // Nếu scope là HOTEL_ONLY thì cần gửi list này
    private List<Long> appliedHotelIds;

    // Nếu scope là TOUR_ONLY
    private List<Long> appliedTourIds;

    // Nếu scope là FLIGHT_ONLY
    private List<Long> appliedFlightIds;

    private Boolean forNewUsersOnly;
    private List<Long> appliedLocationIds;

    // --- NOTIFICATION OPTIONS ---
    private Boolean sendNotification; // Có gửi thông báo không?
    private String notificationTitle; // Tiêu đề thông báo
    private String notificationMessage; // Nội dung thông báo
}