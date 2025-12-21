package com.example.dacn2.service.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dacn2.dto.response.admin.TotalResponse;
import com.example.dacn2.repository.BookingRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.tour.TourRepository;

@Service
public class AdminService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TourRepository tourRepository;

    public TotalResponse getTotal() {
        return new TotalResponse(
                hotelRepository.countTotalHotels(),
                bookingRepository.countTotalBooking(),
                tourRepository.countTotalTours(),
                bookingRepository.countTotalRevenue());
    }
}
