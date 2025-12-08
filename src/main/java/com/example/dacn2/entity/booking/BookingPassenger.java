package com.example.dacn2.entity.booking;

import java.time.LocalDate;

import com.example.dacn2.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassenger extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private String fullName; // Họ tên hành khách
    private String gender; // Giới tính (MALE, FEMALE)
    private LocalDate dateOfBirth; // Ngày sinh (dd/MM/yyyy)
    private String nationality; // Quốc tịch
    private String idNumber; // CMND/CCCD/Passport
    private String phoneNumber; // SĐT (nếu có)

    // Loại hành khách (Adult, Child, Infant)
    private String passengerType;
}
