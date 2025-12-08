package com.example.dacn2.entity.booking;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentMethod {
    CREDIT_CARD, // Thẻ tín dụng
    ATM, // Thẻ nội địa
    MOMO, // Ví điện tử
    PAY_LATER // Thanh toán sau
}