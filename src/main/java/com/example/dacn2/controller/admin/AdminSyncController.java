package com.example.dacn2.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.entity.tour.TourDocument;
import com.example.dacn2.repository.tour.TourESRepository;
import com.example.dacn2.repository.tour.TourRepository;

@RestController
@RequestMapping("/api/admin/sync")
public class AdminSyncController {

    @Autowired
    private TourRepository repository;
    @Autowired
    private TourESRepository esRepository;

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

}
