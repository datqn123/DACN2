package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.flight.AirlineRequest;
import com.example.dacn2.entity.flight.Airline;
import com.example.dacn2.repository.flight.AirlineRepository;
import com.example.dacn2.repository.flight.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    public List<Airline> getAll() {
        return airlineRepository.findAll();
    }

    public Airline getById(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng bay với id: " + id));
    }

    public Airline create(AirlineRequest request) {
        Airline airline = new Airline();
        airline.setName(request.getName());
        airline.setCode(request.getCode());
        airline.setLogoUrl(request.getLogoUrl());
        return airlineRepository.save(airline);
    }

    public Airline update(Long id, AirlineRequest request) {
        Airline airline = getById(id);
        airline.setName(request.getName());
        airline.setCode(request.getCode());
        airline.setLogoUrl(request.getLogoUrl());
        return airlineRepository.save(airline);
    }

    public void delete(Long id) {
        if (flightRepository.existsByAirlineId(id)) {
            throw new RuntimeException("Không thể xóa hãng bay này vì đang có chuyến bay hoạt động!");
        }
        airlineRepository.deleteById(id);
    }
}
