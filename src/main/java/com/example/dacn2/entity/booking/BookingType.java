package com.example.dacn2.entity.booking;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BookingType {
    HOTEL,
    FLIGHT,
    TOUR
}