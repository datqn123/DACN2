package com.example.dacn2.service.hotel_service;

import com.example.dacn2.dto.request.hotel.HotelRequest;
import com.example.dacn2.entity.hotel.HotelImage;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.hotel.Amenity;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelView;
import com.example.dacn2.repository.hotel.AmenityRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository; // Hoặc LocationRepository tùy tên bạn đặt
import com.example.dacn2.service.FileUploadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private FileUploadService fileUploadService;

    // 1. Lấy tất cả
    public List<Hotel> getAll() {
        return hotelRepository.findAll();
    }

    // 2. Lấy chi tiết
    public Hotel getById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn ID: " + id));
    }

    // 3. Tạo mới (Create)
    @Transactional
    public Hotel create(HotelRequest request, List<MultipartFile> images) {
        // Check trùng lặp (Tên + Địa chỉ)
        if (hotelRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new RuntimeException("Khách sạn '" + request.getName() + "' tại địa chỉ này đã tồn tại!");
        }

        Hotel hotel = new Hotel();

        // Map thông tin cơ bản
        mapBasicInfo(request, hotel);

        // Xử lý ảnh (Thêm mới)
        try {
            processImages(request.getImageUrls(), images, hotel);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
        }

        return hotelRepository.save(hotel);
    }

    // 4. Cập nhật (Update)
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

    // 5. Xóa (Delete)
    @Transactional
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách sạn để xóa");
        }
        hotelRepository.deleteById(id);
    }

    // --- HÀM PHỤ TRỢ 1: Map thông tin cơ bản ---
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

    // --- HÀM PHỤ TRỢ 2: Xử lý ảnh (Upload & Gán) ---
    private void processImages(List<String> urlLinks, List<MultipartFile> files, Hotel hotel) throws IOException {

        // Lấy danh sách ảnh hiện tại (Nếu null thì tạo mới để tránh lỗi NullPointer)
        List<HotelImage> currentImages = hotel.getImages();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }

        // 1. Xử lý Link ảnh string (Copy paste)
        if (urlLinks != null && !urlLinks.isEmpty()) {
            for (String url : urlLinks) {
                HotelImage image = new HotelImage();
                image.setImageUrl(url);
                image.setHotel(hotel);
                currentImages.add(image);
            }
        }

        // 2. Xử lý File upload (Multipart)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Gọi service upload lên Cloud
                String urlFromCloud = fileUploadService.uploadFile(file);

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