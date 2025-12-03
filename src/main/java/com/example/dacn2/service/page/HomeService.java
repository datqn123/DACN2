package com.example.dacn2.service.page;

import com.example.dacn2.dto.response.home.LocationCardResponse;
import com.example.dacn2.entity.Location;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {

    @Autowired
    private LocationInterfaceRepository locationRepository;

    public List<LocationCardResponse> getFeaturedLocations() {
        List<Location> locations = locationRepository.findFeaturedLocations();

        return locations.stream().map(location ->
                LocationCardResponse.builder()
                        .id(location.getId())
                        .name(location.getName())
                        .slug(location.getSlug())
                        .thumbnail(location.getThumbnail())
                        .build()
        ).collect(Collectors.toList());
    }
}