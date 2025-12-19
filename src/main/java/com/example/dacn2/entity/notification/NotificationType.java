package com.example.dacn2.entity.notification;

/**
 * Enum định nghĩa các loại thông báo trong hệ thống
 */
public enum NotificationType {
    // Thông báo liên quan đến đặt phòng/vé
    BOOKING_CREATED, // Đặt phòng/vé thành công
    PAYMENT_SUCCESS, // Thanh toán thành công
    BOOKING_CANCELLED, // Đơn hàng bị hủy
    BOOKING_REMINDER, // Nhắc nhở check-in/khởi hành

    // Thông báo khuyến mãi
    PROMOTION, // Có khuyến mãi mới
    VOUCHER_EXPIRING, // Voucher sắp hết hạn

    // Thông báo hệ thống
    SYSTEM, // Thông báo từ hệ thống
    ACCOUNT // Thông báo liên quan tài khoản
}
