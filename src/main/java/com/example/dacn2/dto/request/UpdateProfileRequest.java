package com.example.dacn2.dto.request;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String fullName; // Họ tên
    private String phoneNumber; // Số điện thoại
    private String gender; // Giới tính: "MALE", "FEMALE", "OTHER"
    private LocalDate dateOfBirth; // Ngày sinh
    private String nationality; // Quốc tịch
    private String address; // Địa chỉ
    private String avatarUrl; // URL ảnh đại diện
}
