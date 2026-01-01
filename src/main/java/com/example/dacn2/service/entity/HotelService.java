package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.hotel.HotelRequest;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.entity.hotel.HotelImage;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.hotel.Amenity;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelDocument;
import com.example.dacn2.entity.hotel.HotelView;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.entity.tour.TourDocument;
import com.example.dacn2.repository.hotel.AmenityRepository;
import com.example.dacn2.repository.hotel.HotelESRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository; // Hoặc LocationRepository tùy tên bạn đặt
import com.example.dacn2.service.user_service.FileUploadService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private HotelESRepository hotelESRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public List<Hotel> getAll() {
        return hotelRepository.findAll();
    }

    public HotelSearchResponse getAllNavigate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<HotelCardResponse> hotelPage = hotelRepository.findAllHotelCards(pageable);

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

    public Hotel getById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn ID: " + id));
    }

    @Transactional
    public Hotel create(HotelRequest request, List<MultipartFile> images) {
        if (hotelRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new RuntimeException("Khách sạn '" + request.getName() + "' tại địa chỉ này đã tồn tại!");
        }

        Hotel hotel = new Hotel();

        mapBasicInfo(request, hotel);

        try {
            processImages(request.getImageUrls(), images, hotel);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        updateMinPriceFromRooms(savedHotel.getId());
        return savedHotel;
    }

    public List<HotelDocument> searchHotelWithEs(String keyword) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("name", "description", "type")
                                .query(keyword)
                                .fuzziness("AUTO")))
                .build();
        SearchHits<HotelDocument> productHits = elasticsearchOperations.search(query, HotelDocument.class);
        return productHits.stream().map(SearchHit::getContent).toList();
    }

    @Transactional
    public Hotel update(Long id, HotelRequest request, List<MultipartFile> images) {
        Hotel hotel = getById(id);
        mapBasicInfo(request, hotel);
        try {
            processImages(request.getImageUrls(), images, hotel);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        // Tự động cập nhật giá từ phòng rẻ nhất
        updateMinPriceFromRooms(updatedHotel.getId());
        return updatedHotel;
    }

    @Transactional
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách sạn để xóa");
        }
        hotelRepository.deleteById(id);
    }

    @Cacheable(value = "hotelByLocation", key = "#locationId")
    public List<HotelCardResponse> getHotelByLocation(Long locationId) {
        return hotelRepository.findByLocationId(locationId);
    }

    private HotelCardResponse convertToCard(Hotel hotel) {
        HotelCardResponse dto = new HotelCardResponse();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setAddress(hotel.getAddress());
        dto.setStarRating(hotel.getStarRating());
        dto.setTotalReviews(hotel.getTotalReviews());

        if (hotel.getLocation() != null) {
            dto.setLocationName(hotel.getLocation().getName());
        }

        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            dto.setThumbnail(hotel.getImages().get(0).getImageUrl());
        }

        dto.setMinPrice(hotel.getPricePerNightFrom());

        if (hotel.getType() != null) {
            dto.setHotelType(hotel.getType().toString());
        }

        return dto;
    }

    private void mapBasicInfo(HotelRequest request, Hotel hotel) {
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setDescription(request.getDescription());
        hotel.setStarRating(request.getStarRating());
        hotel.setType(request.getType());
        hotel.setCheckInTime(request.getCheckInTime());
        hotel.setCheckOutTime(request.getCheckOutTime());
        hotel.setContactPhone(request.getContactPhone());
        hotel.setContactEmail(request.getContactEmail());
        if (request.getViewTypes() != null) {
            Set<HotelView> views = new HashSet<>();
            for (String viewStr : request.getViewTypes()) {
                try {
                    views.add(HotelView.valueOf(viewStr));
                } catch (IllegalArgumentException e) {
                    // Bỏ qua giá trị không hợp lệ
                }
            }
            hotel.setViews(views);
        }

        // Gán Location
        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location ID không tồn tại"));
            hotel.setLocation(location);
        }

        // Gán Amenities
        if (request.getAmenityIds() != null) {
            List<Amenity> amenities = amenityRepository.findAllById(request.getAmenityIds());
            hotel.setAmenities(new HashSet<>(amenities));
        }
    }

    private void processImages(List<String> urlLinks, List<MultipartFile> files, Hotel hotel) throws IOException {

        List<HotelImage> currentImages = hotel.getImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }

        if (urlLinks != null && !urlLinks.isEmpty()) {
            for (String url : urlLinks) {
                if (url == null || url.trim().isEmpty()) {
                    continue;
                }

                HotelImage image = new HotelImage();
                image.setImageUrl(url);
                image.setHotel(hotel);
                currentImages.add(image);
            }
        }

        // 2. Xử lý File upload (Multipart)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Bỏ qua file rỗng
                if (file.isEmpty()) {
                    continue;
                }

                // Gọi service upload lên Cloud
                String urlFromCloud = fileUploadService.uploadFile(file);

                // Kiểm tra xem upload có thành công không
                if (urlFromCloud == null || urlFromCloud.trim().isEmpty()) {
                    continue;
                }

                HotelImage image = new HotelImage();
                image.setImageUrl(urlFromCloud);
                image.setHotel(hotel);
                currentImages.add(image);
            }
        }

        // Cập nhật lại danh sách ảnh cho Hotel
        hotel.setImages(currentImages);
    }

    @Transactional
    public void updateMinPriceFromRooms(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel không tồn tại"));

        Double minPrice = hotel.getRooms() != null && !hotel.getRooms().isEmpty()
                ? hotel.getRooms().stream()
                        .filter(room -> room.getPrice() != null && room.getPrice() > 0)
                        .map(Room::getPrice)
                        .min(Double::compareTo)
                        .orElse(null)
                : null;

        hotel.setPricePerNightFrom(minPrice);
        HotelDocument hotelDocument = HotelDocument.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .type(hotel.getType())
                .pricePerNightFrom(hotel.getPricePerNightFrom())
                .thumbnail(hotel.getImages().get(0).getImageUrl())
                .build();
        hotelESRepository.save(hotelDocument);
        hotelRepository.save(hotel);
    }
}