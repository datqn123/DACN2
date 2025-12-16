package com.example.dacn2.service.user_service;

import com.example.dacn2.dto.request.SearchHistoryRequest;
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
import com.example.dacn2.service.entity.SearchHistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchHotelService {

    private static final int PAGE_SIZE = 20;

    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private SearchHistoryService searchHistoryService;
    @Autowired
    private com.example.dacn2.repository.auth.AccountRepositoryInterface accountRepository;

    /**
     * Helper: Lấy accountId từ UserDetails (dùng chung cho các Controller)
     */
    public Long getAccountIdFromUserDetails(org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails == null)
            return null;
        return accountRepository.findByEmail(userDetails.getUsername())
                .map(account -> account.getId())
                .orElse(null);
    }

    /**
     * Lấy 10 địa điểm phổ biến nhất
     */
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
     * SỬ DỤNG DTO PROJECTION + DATABASE-LEVEL PAGINATION (tối ưu hiệu suất tối đa)
     */
    public HotelSearchResponse searchHotelsWithFilter(HotelFilterRequest filter) {
        // Tạo Pageable với sort theo giá nếu có yêu cầu
        int page = filter.getPage() != null ? filter.getPage() : 0;
        Sort sort = Sort.unsorted();
        if ("ASC".equalsIgnoreCase(filter.getSortByPrice())) {
            sort = Sort.by(Sort.Direction.ASC, "pricePerNightFrom");
        } else if ("DESC".equalsIgnoreCase(filter.getSortByPrice())) {
            sort = Sort.by(Sort.Direction.DESC, "pricePerNightFrom");
        }
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

        // Query trực tiếp DTO Projection - CHỈ LẤY CÁC CỘT CẦN THIẾT
        Page<HotelCardResponse> hotelPage = hotelRepository.findHotelCardsWithFilters(
                filter.getLocationSlug(),
                filter.getMinStarRating(),
                filter.getHotelType(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                pageable);

        return HotelSearchResponse.builder()
                .hotels(hotelPage.getContent())
                .currentPage(hotelPage.getNumber())
                .totalPages(hotelPage.getTotalPages())
                .totalElements(hotelPage.getTotalElements())
                .pageSize(hotelPage.getSize())
                .hasNext(hotelPage.hasNext())
                .hasPrevious(hotelPage.hasPrevious())
                .build();
    }

    /**
     * Tìm khách sạn + Tự động lưu lịch sử (nếu user đã đăng nhập)
     * Dùng cho Controller, tái sử dụng cho Flight, Tour...
     */
    public HotelSearchResponse searchHotelsAndSaveHistory(HotelFilterRequest filter, Long accountId) {
        HotelSearchResponse result = searchHotelsWithFilter(filter);

        // Lưu lịch sử nếu có accountId và có keyword
        if (accountId != null && filter.getLocationSlug() != null && !filter.getLocationSlug().isBlank()) {
            SearchHistoryRequest historyRequest = SearchHistoryRequest.builder()
                    .keyword(filter.getLocationSlug())
                    .searchType("HOTEL")
                    .resultCount((int) result.getTotalElements())
                    .minPrice(filter.getMinPrice())
                    .maxPrice(filter.getMaxPrice())
                    .starRating(filter.getMinStarRating())
                    .hotelType(filter.getHotelType() != null ? filter.getHotelType().name() : null)
                    .build();

            searchHistoryService.saveFromRequest(accountId, historyRequest);
        }

        return result;
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
