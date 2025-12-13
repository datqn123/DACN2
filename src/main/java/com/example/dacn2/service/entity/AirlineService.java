package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.flight.AirlineRequest;
import com.example.dacn2.entity.flight.Airline;
import com.example.dacn2.repository.flight.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;

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
        airlineRepository.deleteById(id);
    }
}
