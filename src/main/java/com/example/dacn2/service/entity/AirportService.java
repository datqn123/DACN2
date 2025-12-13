package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.flight.AirportRequest;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.flight.Airport;
import com.example.dacn2.repository.flight.AirportRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final LocationInterfaceRepository locationRepository;

    public List<Airport> getAll() {
        return airportRepository.findAll();
    }

    public Airport getById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bay với id: " + id));
    }

    public Airport create(AirportRequest request) {
        Airport airport = new Airport();
        airport.setName(request.getName());
        airport.setCode(request.getCode());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm"));
            airport.setLocation(location);
        }

        return airportRepository.save(airport);
    }

    public Airport update(Long id, AirportRequest request) {
        Airport airport = getById(id);
        airport.setName(request.getName());
        airport.setCode(request.getCode());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm"));
            airport.setLocation(location);
        }

        return airportRepository.save(airport);
    }

    public void delete(Long id) {
        airportRepository.deleteById(id);
    }
}
