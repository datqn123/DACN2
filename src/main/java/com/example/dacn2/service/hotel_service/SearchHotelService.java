package com.example.dacn2.service.hotel_service;

import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.repository.flight.FlightRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchHotelService {

    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;

    public List<LocationSearchResult> findTopDestinations() {
        Pageable top10 = PageRequest.of(0, 10);
        return locationRepository.findTopDestinations(top10);
    }

    public List<LocationSearchResult> searchLocationDropdown(String keyword) {
        Pageable top10 = PageRequest.of(0, 10);
        if (keyword == null || keyword.trim().isEmpty()) {
            return locationRepository.findTopDestinations(top10);
        }
        // Nếu có keyword -> Tìm kiếm
        return locationRepository.searchLocationsWithHotelCount(keyword, top10);
    }

}
