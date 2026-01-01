package com.example.dacn2.entity.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "hotels")
public class HotelDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private HotelType type;

    @Field(type = FieldType.Double)
    private Double pricePerNightFrom;

    @Field(type = FieldType.Keyword)
    private String thumbnail;
}