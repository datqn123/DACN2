package com.example.dacn2.repository.hotel;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.dacn2.entity.hotel.HotelDocument;

public interface HotelESRepository extends ElasticsearchRepository<HotelDocument, Long> {

}
