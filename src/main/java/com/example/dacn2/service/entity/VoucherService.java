package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.voucher.VoucherRequest;
import com.example.dacn2.dto.response.HotelSummary;
import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.dto.response.home.VoucherCardResponse;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.entity.Location;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;
import com.example.dacn2.repository.voucher.VoucherRepository;
import com.example.dacn2.service.user_service.FileUploadService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private LocationInterfaceRepository locationsRepository;
    @Autowired
    private FileUploadService fileUploadService;

    public List<VoucherResponse> getAll() {
        return voucherRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public Voucher create(VoucherRequest request, MultipartFile image) throws IOException {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã Voucher '" + request.getCode() + "' đã tồn tại!");
        }

        Voucher voucher = new Voucher();
        mapRequestToEntity(request, voucher, image);

        return voucherRepository.save(voucher);
    }

    @Transactional
    public Voucher update(Long id, VoucherRequest request, MultipartFile image) throws IOException {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        // Nếu đổi code thì phải check trùng
        if (!voucher.getCode().equals(request.getCode()) && voucherRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã Voucher '" + request.getCode() + "' đã tồn tại!");
        }

        mapRequestToEntity(request, voucher, image);
        return voucherRepository.save(voucher);
    }

    @Transactional
    public void delete(Long id) {
        voucherRepository.deleteById(id);
    }

    public Voucher getById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
    }

    public List<VoucherCardResponse> getVouchersToHome() {
        Pageable pageable = PageRequest.of(0, 3);
        List<Voucher> vouchers = voucherRepository.get3VoucherForHome(pageable);
        return vouchers.stream()
                .map(this::toCardResponse)
                .toList();
    }

    public List<VoucherCardResponse> getVoucherToHotelPage() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Voucher> vouchers = voucherRepository.get5VoucherForHotelPage(pageable);
        return vouchers.stream()
                .map(this::toCardResponse)
                .toList();
    }

    private VoucherCardResponse toCardResponse(Voucher voucher) {
        return VoucherCardResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .image(voucher.getImage())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .isActive(voucher.getIsActive())
                .build();
    }

    private VoucherResponse toResponse(Voucher voucher) {
        List<HotelSummary> hotelSummaries = voucher.getAppliedHotels().stream()
                .map(hotel -> HotelSummary.builder()
                        .id(hotel.getId())
                        .name(hotel.getName())
                        .address(hotel.getAddress())
                        .starRating(hotel.getStarRating())
                        .averageRating(hotel.getAverageRating())
                        .image(hotel.getImages() != null && !hotel.getImages().isEmpty()
                                ? hotel.getImages().get(0).getImageUrl()
                                : null)
                        .lowestPrice(hotel.getRooms().stream()
                                .mapToDouble(r -> r.getPrice())
                                .min().orElse(0))
                        .build())
                .toList();
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .image(voucher.getImage())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minOrderValue(voucher.getMinOrderValue())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .usageLimit(voucher.getUsageLimit())
                .usageCount(voucher.getUsageCount())
                .userLimit(voucher.getUserLimit())
                .isActive(voucher.getIsActive())
                .scope(voucher.getScope())
                .appliedHotels(hotelSummaries)
                .build();
    }

    // --- HÀM MAP DỮ LIỆU ---
    private void mapRequestToEntity(VoucherRequest request, Voucher voucher, MultipartFile image) throws IOException {
        voucher.setCode(request.getCode());
        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUserLimit(request.getUserLimit());
        voucher.setScope(request.getScope());
        voucher.setForNewUsersOnly(Boolean.TRUE.equals(request.getForNewUsersOnly()));

        if (request.getAppliedLocationIds() != null && !request.getAppliedLocationIds().isEmpty()) {
            List<Location> locations = locationsRepository.findAllById(request.getAppliedLocationIds());
            voucher.setAppliedLocations(new HashSet<>(locations));
        } else {
            // Nếu gửi list rỗng thì xóa liên kết cũ (trường hợp update)
            voucher.setAppliedLocations(new HashSet<>());
        }

        // Xử lý ảnh (Ưu tiên file upload, sau đó đến link text)
        if (image != null && !image.isEmpty()) {
            String url = fileUploadService.uploadFile(image);
            voucher.setImage(url);
        } else if (request.getImageUrl() != null) {
            voucher.setImage(request.getImageUrl());
        }

        // Xử lý quan hệ (Scope)
        // 1. Khách sạn
        if (request.getAppliedHotelIds() != null && !request.getAppliedHotelIds().isEmpty()) {
            List<Hotel> hotels = hotelRepository.findAllById(request.getAppliedHotelIds());
            voucher.setAppliedHotels(new HashSet<>(hotels));
        }

        // 2. Tour
        if (request.getAppliedTourIds() != null && !request.getAppliedTourIds().isEmpty()) {
            List<Tour> tours = tourRepository.findAllById(request.getAppliedTourIds());
            voucher.setAppliedTours(new HashSet<>(tours));
        }
    }
}