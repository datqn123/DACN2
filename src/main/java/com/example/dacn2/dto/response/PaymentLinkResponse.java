package com.example.dacn2.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentLinkResponse {
    private Long orderCode; // Mã đơn hàng
    private Long amount; // Số tiền
    private String description; // Nội dung
    private String checkoutUrl; // Link thanh toán (nếu muốn redirect)
    private String qrCode; // URL ảnh QR code để hiển thị
    private String accountNumber; // Số tài khoản
    private String accountName; // Tên chủ tài khoản
    private String bankName; // Tên ngân hàng
}
