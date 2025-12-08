package com.example.dacn2.dto.request;

import lombok.Data;

@Data
public class PassengerRequest {
    private String fullName; // Họ tên hành khách
    private String gender; // Giới tính: MALE, FEMALE
    private String dob; // Ngày sinh: "2000-01-01"
    private String nationality; // Quốc tịch
    private String idNumber; // CMND/CCCD/Passport
    private String phoneNumber; // SĐT (nếu có)
    private String type; // Loại hành khách: ADULT, CHILD, INFANT
}