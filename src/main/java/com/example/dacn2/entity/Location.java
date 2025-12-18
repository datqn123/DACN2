package com.example.dacn2.entity;

import com.example.dacn2.entity.enums.LocationType;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.tour.Tour;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "locations")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    private LocationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    @ToString.Exclude
    private Location parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<Location> children;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private List<Hotel> hotels;

    @OneToMany(mappedBy = "startLocation")
    @JsonIgnore
    @ToString.Exclude
    private List<Tour> toursStartingHere;

    @OneToMany(mappedBy = "destination")
    @JsonIgnore
    @ToString.Exclude
    private List<Tour> toursEndingHere;
}
