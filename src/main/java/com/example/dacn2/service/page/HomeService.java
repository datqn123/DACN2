package com.example.dacn2.service.page;

import com.example.dacn2.dto.response.home.FlightCardResponse;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.LocationCardResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.entity.flight.FlightSeat;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.repository.flight.FlightRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {

    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;

    public List<LocationCardResponse> getFeaturedLocations() {
        List<Location> locations = locationRepository.findFeaturedLocations();

        return locations.stream().map(location -> LocationCardResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .slug(location.getSlug())
                .thumbnail(location.getThumbnail())

                .build()).collect(Collectors.toList());
    }

    public List<FlightCardResponse> getFeaturedFlights() {
        Pageable top5Flights = PageRequest.of(0, 5);
        List<Flight> flights = flightRepository.findTopDeals(top5Flights);
        return flights.stream()
                .distinct()
                .limit(5)
                .map(this::convertToCard)
                .collect(Collectors.toList());
    }

    public List<HotelCardResponse> getFeaturedHotels() {
        Pageable top5Hotels = PageRequest.of(0, 10);
        List<Hotel> hotels = hotelRepository.findFeaturedHotels(top5Hotels);

        return hotels.stream()
                .map(this::convertToHotelCard)
                .collect(Collectors.toList());
    }

    public List<TourCardResponse> getFeaturedTours() {
        Pageable top5Tours = PageRequest.of(0, 10);
        List<Tour> tours = tourRepository.findFeaturedTours(top5Tours);

        return tours.stream()
                .map(this::convertToTourCard)
                .collect(Collectors.toList());
    }

    private TourCardResponse convertToTourCard(Tour tour) {
        TourCardResponse dto = new TourCardResponse();
        dto.setId(tour.getId());
        dto.setTitle(tour.getTitle());
        dto.setSlug(tour.getSlug());
        dto.setDuration(tour.getDuration());

        // Nơi khởi hành và đích đến
        dto.setStartLocationName(tour.getStartLocation().getName());
        dto.setDestinationName(tour.getDestination().getName());

        // Thumbnail - có thể dùng field thumbnail hoặc lấy từ images[0]
        if (tour.getThumbnail() != null) {
            dto.setThumbnail(tour.getThumbnail());
        } else if (tour.getImages() != null && !tour.getImages().isEmpty()) {
            dto.setThumbnail(tour.getImages().get(0).getImageUrl());
        }

        // Giá - Tour có 3 field: price, priceAdult, priceChild
        // Ưu tiên dùng field 'price', nếu null thì dùng priceAdult
        dto.setPrice(tour.getPrice() != null ? tour.getPrice() : tour.getPriceAdult());

        dto.setTransportation(tour.getTransportation());

        return dto;
    }

    private HotelCardResponse convertToHotelCard(Hotel hotel) {
        HotelCardResponse dto = new HotelCardResponse();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setStarRating(hotel.getStarRating());
        dto.setLocationName(hotel.getLocation().getName());

        // Lấy ảnh đầu tiên làm thumbnail
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            dto.setThumbnail(hotel.getImages().get(0).getImageUrl());
        }

        // Giá phòng thấp nhất: ưu tiên dùng pricePerNightFrom, không thì tính từ rooms
        if (hotel.getPricePerNightFrom() != null) {
            dto.setMinPrice(hotel.getPricePerNightFrom());
        } else if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            double minPrice = hotel.getRooms().stream()
                    .filter(room -> room.getPrice() != null && room.getIsAvailable())
                    .mapToDouble(Room::getPrice)
                    .min()
                    .orElse(0.0);
            dto.setMinPrice(minPrice);
        }

        // Hotel type
        if (hotel.getType() != null) {
            dto.setHotelType(hotel.getType().toString());
        }

        return dto;
    }

    private FlightCardResponse convertToCard(Flight flight) {
        FlightCardResponse dto = new FlightCardResponse();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirlineLogo(flight.getAirline().getLogoUrl());
        dto.setAirlineName(flight.getAirline().getName());

        // Format ngày giờ cho đẹp (Frontend đỡ phải xử lý)
        dto.setDepartureTime(flight.getDepartureTime().toLocalTime().toString());
        dto.setArrivalTime(flight.getArrivalTime().toLocalTime().toString());
        dto.setDuration(flight.getDuration());

        dto.setFromLocation(flight.getDepartureAirport().getLocation().getName());
        dto.setToLocation(flight.getArrivalAirport().getLocation().getName());

        // Lấy giá nhỏ nhất trong list ghế
        double minPrice = flight.getFlightSeats().stream()
                .mapToDouble(FlightSeat::getPrice)
                .min()
                .orElse(0.0);
        dto.setMinPrice(minPrice);

        // Lấy ảnh của điểm đến (location của sân bay đến)
        dto.setImage(flight.getArrivalAirport().getLocation().getThumbnail());

        return dto;
    }
}
