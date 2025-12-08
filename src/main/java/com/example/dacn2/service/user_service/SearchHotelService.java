package com.example.dacn2.service.user_service;

import com.example.dacn2.dto.request.hotel.HotelFilterRequest;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.repository.flight.FlightRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.hotel.HotelSpecification;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SearchHotelService {

    private static final int PAGE_SIZE = 20; // Số lượng kết quả mỗi trang

    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;

    public List<LocationSearchResult> findTopDestinations() {
        Pageable top10 = PageRequest.of(0, 10);
        return locationRepository.findTopDestinations(top10);
    }

    public List<LocationSearchResult> searchLocationDropdown(String keyword) {
        Pageable top10 = PageRequest.of(0, 10);
        if (keyword == null || keyword.trim().isEmpty()) {
            return locationRepository.findTopDestinations(top10);
        }
        // Nếu có keyword -> Tìm kiếm
        return locationRepository.searchLocationsWithHotelCount(keyword, top10);
    }

    /**
     * Tìm khách sạn theo tên địa điểm (VD: "Đà Nẵng", "Singapore")
     * Query sẽ tìm trong location hiện tại hoặc parent location
     */
    public List<HotelCardResponse> searchHotelsByLocationSlug(String slug) {
        return hotelRepository.findByLocationSlug(slug).stream()
                .map(this::convertToHotelCard)
                .toList();
    }

    /**
     * Tìm khách sạn với bộ lọc linh hoạt và phân trang
     * Mỗi trang hiển thị 20 kết quả
     */
    public HotelSearchResponse searchHotelsWithFilter(HotelFilterRequest filter) {
        // Lấy tất cả kết quả phù hợp filter
        List<HotelCardResponse> allResults = hotelRepository.findAll(HotelSpecification.withFilters(filter)).stream()
                .map(this::convertToHotelCard)
                .toList();

        // Sắp xếp theo giá nếu có yêu cầu
        allResults = sortByPrice(allResults, filter.getSortByPrice());

        // Tính toán phân trang
        int page = filter.getPage() != null ? filter.getPage() : 0;
        int totalElements = allResults.size();
        int totalPages = (int) Math.ceil((double) totalElements / PAGE_SIZE);

        // Lấy kết quả cho trang hiện tại
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalElements);

        List<HotelCardResponse> pagedResults;
        if (startIndex >= totalElements) {
            pagedResults = List.of(); // Trang không có dữ liệu
        } else {
            pagedResults = allResults.subList(startIndex, endIndex);
        }

        return HotelSearchResponse.builder()
                .hotels(pagedResults)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .pageSize(PAGE_SIZE)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }

    /**
     * Sắp xếp danh sách theo giá
     */
    private List<HotelCardResponse> sortByPrice(List<HotelCardResponse> results, String sortByPrice) {
        if (sortByPrice == null) {
            return results;
        }

        Comparator<HotelCardResponse> comparator;
        if ("ASC".equalsIgnoreCase(sortByPrice)) {
            // Giá thấp đến cao (null giá đẩy xuống cuối)
            comparator = (h1, h2) -> {
                Double p1 = h1.getMinPrice() != null ? h1.getMinPrice() : Double.MAX_VALUE;
                Double p2 = h2.getMinPrice() != null ? h2.getMinPrice() : Double.MAX_VALUE;
                return p1.compareTo(p2);
            };
        } else if ("DESC".equalsIgnoreCase(sortByPrice)) {
            // Giá cao đến thấp (null giá đẩy xuống cuối)
            comparator = (h1, h2) -> {
                Double p1 = h1.getMinPrice() != null ? h1.getMinPrice() : 0.0;
                Double p2 = h2.getMinPrice() != null ? h2.getMinPrice() : 0.0;
                return p2.compareTo(p1);
            };
        } else {
            return results;
        }

        return results.stream().sorted(comparator).toList();
    }

    /**
     * Chuyển đổi Hotel entity sang HotelCardResponse DTO
     * minPrice: ưu tiên pricePerNightFrom, fallback sang tính từ rooms
     */
    private HotelCardResponse convertToHotelCard(Hotel hotel) {
        Double minPrice = hotel.getPricePerNightFrom();

        // Nếu pricePerNightFrom null -> tính từ rooms
        if (minPrice == null && hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            minPrice = hotel.getRooms().stream()
                    .filter(room -> room.getPrice() != null && room.getIsAvailable())
                    .mapToDouble(Room::getPrice)
                    .min()
                    .orElse(0.0);
        }

        return HotelCardResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .starRating(hotel.getStarRating())
                .locationName(hotel.getLocation() != null ? hotel.getLocation().getName() : null)
                .thumbnail(hotel.getImages() != null && !hotel.getImages().isEmpty()
                        ? hotel.getImages().get(0).getImageUrl()
                        : null)
                .minPrice(minPrice)
                .hotelType(hotel.getType() != null ? hotel.getType().name() : null)
                .build();
    }

}
