package com.example.dacn2.service;

import com.example.dacn2.dto.request.voucher.VoucherRequest;
import com.example.dacn2.entity.*;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.repository.flight.FlightRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.tour.TourRepository;
import com.example.dacn2.repository.voucher.VoucherRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class VoucherService {

    @Autowired private VoucherRepository voucherRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private TourRepository tourRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private FileUploadService fileUploadService;

    public List<Voucher> getAll() {
        return voucherRepository.findAll();
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