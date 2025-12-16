package com.example.dacn2.service.page;

import com.example.dacn2.dto.response.home.FlightCardResponse;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.LocationCardResponse;
import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.entity.flight.FlightSeat;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.repository.flight.FlightRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HomeService {

    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;

    /**
     * Lấy địa điểm nổi bật - Sử dụng DTO Projection
     */
    public List<LocationCardResponse> getFeaturedLocations() {
        return locationRepository.findFeaturedLocationCards();
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

    /**
     * Lấy tour nổi bật - Sử dụng DTO Projection
     */
    public List<TourCardResponse> getFeaturedTours() {
        Pageable top5Tours = PageRequest.of(0, 10);
        return tourRepository.findFeaturedTourCards(top5Tours);
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
