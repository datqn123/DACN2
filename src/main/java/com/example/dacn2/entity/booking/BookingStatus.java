package com.example.dacn2.entity.booking;

public enum BookingStatus {
    PENDING, // Đang chờ thanh toán
    CONFIRMED, // Đã thanh toán & Xác nhận thành công
    CANCELLED, // Đã hủy (bởi khách hoặc admin)
    COMPLETED, // Đã đi/đã ở xong (Hoàn tất)
    REFUNDED // Đã hoàn tiền
}