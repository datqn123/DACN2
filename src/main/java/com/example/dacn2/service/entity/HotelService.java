package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.hotel.HotelRequest;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.entity.hotel.HotelImage;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.hotel.Amenity;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelView;
import com.example.dacn2.repository.hotel.AmenityRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository; // Hoặc LocationRepository tùy tên bạn đặt
import com.example.dacn2.service.user_service.FileUploadService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        return hotelRepository.save(hotel);
    }

    @Transactional
    public Hotel update(Long id, HotelRequest request, List<MultipartFile> images) {
        Hotel hotel = getById(id); // Tìm khách sạn cũ

        // Map thông tin mới đè lên cũ
        mapBasicInfo(request, hotel);

        // Xử lý ảnh (Thêm vào danh sách hiện có)
        try {
            processImages(request.getImageUrls(), images, hotel);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }

        return hotelRepository.save(hotel);
    }

    @Transactional
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách sạn để xóa");
        }
        hotelRepository.deleteById(id);
    }

    // lấy hotel theo location
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

        // Lấy ảnh đầu tiên làm thumbnail
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            dto.setThumbnail(hotel.getImages().get(0).getImageUrl());
        }

        // Giá phòng thấp nhất
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

        // Lấy danh sách ảnh hiện tại (Nếu null thì tạo mới để tránh lỗi NullPointer)
        List<HotelImage> currentImages = hotel.getImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }

        // 1. Xử lý Link ảnh string (Copy paste)
        if (urlLinks != null && !urlLinks.isEmpty()) {
            for (String url : urlLinks) {
                // Bỏ qua URL null hoặc rỗng
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
}