package com.example.dacn2.repository.tour;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.dacn2.entity.tour.TourDocument;

@Repository
public interface TourESRepository extends ElasticsearchRepository<TourDocument, Long> {

}
