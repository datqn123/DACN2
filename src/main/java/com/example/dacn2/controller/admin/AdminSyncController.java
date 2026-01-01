package com.example.dacn2.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelDocument;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.entity.tour.TourDocument;
import com.example.dacn2.repository.hotel.HotelESRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.tour.TourESRepository;
import com.example.dacn2.repository.tour.TourRepository;

@RestController
@RequestMapping("/api/admin/sync")
public class AdminSyncController {

    @Autowired
    private TourRepository repository;
    @Autowired
    private TourESRepository esRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private HotelESRepository hotelESRepository;

    @PostMapping("/tours")
    public ResponseEntity<String> syncTour() {
        esRepository.deleteAll();

        List<Tour> listInDB = repository.findAll();

        List<TourDocument> docs = listInDB.stream().map(tour -> TourDocument.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .price(tour.getPrice())
                .description(tour.getDescription())
                .start_location(tour.getStartLocation().getName())
                .thumbnail(tour.getThumbnail())
                .build()).toList();

        esRepository.saveAll(docs);
        return ResponseEntity.ok("Synced " + docs.size() + " tours to Elasticsearch");
    }

    @PostMapping("/hotels")
    public ResponseEntity<String> syncHotel() {
        hotelESRepository.deleteAll();

        List<Hotel> listInDB = hotelRepository.findAll();

        List<HotelDocument> docs = listInDB.stream().map(hotel -> HotelDocument.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .type(hotel.getType())
                .pricePerNightFrom(hotel.getPricePerNightFrom())
                .thumbnail(hotel.getImages() != null && !hotel.getImages().isEmpty()
                        ? hotel.getImages().get(0).getImageUrl()
                        : "")
                .build()).toList();

        hotelESRepository.saveAll(docs);
        return ResponseEntity.ok("Synced " + docs.size() + " hotels to Elasticsearch");
    }

}
