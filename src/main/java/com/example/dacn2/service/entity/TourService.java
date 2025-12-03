package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.tour.*;
import com.example.dacn2.entity.*;
import com.example.dacn2.entity.tour.*;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;
import com.example.dacn2.service.FileUploadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService {

    @Autowired private TourRepository tourRepository;
    @Autowired private LocationInterfaceRepository locationRepository;
    @Autowired private FileUploadService fileUploadService;

    public List<Tour> getAll() {
        return tourRepository.findAll();
    }

    public Tour getById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Tour ID: " + id));
    }

    public Tour create(TourRequest request, List<MultipartFile> images) {
        // A. Check nhanh dữ liệu (Validation)
        if (tourRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Slug '" + request.getSlug() + "' đã tồn tại!");
        }

        // B. Upload ảnh song song (Nặng về mạng IO)
        List<String> uploadedUrls = uploadImagesInParallel(images);

        // C. Gọi hàm lưu vào DB (Nhanh, gọn, có Transaction)
        return saveTourToDB(new Tour(), request, uploadedUrls);
    }

    public Tour update(Long id, TourRequest request, List<MultipartFile> images) {
        // A. Kiểm tra tồn tại trước khi upload cho đỡ tốn công
        if (!tourRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Tour ID: " + id);
        }

        // B. Upload ảnh song song
        List<String> uploadedUrls = uploadImagesInParallel(images);

        // C. Mở Transaction để tìm và cập nhật
        return updateTourInDB(id, request, uploadedUrls);
    }

    @Transactional
    public void delete(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Tour để xóa");
        }
        tourRepository.deleteById(id);
    }

    @Transactional // Chỉ mở kết nối DB trong phạm vi hàm này
    protected Tour saveTourToDB(Tour tour, TourRequest request, List<String> uploadedUrls) {
        // 1. Map thông tin cơ bản & Lịch trình
        mapBasicInfo(request, tour);

        // 2. Xử lý ảnh (Gộp link text và link vừa upload)
        List<String> allImageLinks = new ArrayList<>();

        // a. Link có sẵn (String)
        if (request.getImageUrls() != null) {
            allImageLinks.addAll(request.getImageUrls());
        }
        // b. Link vừa upload xong
        allImageLinks.addAll(uploadedUrls);

        // 3. Tạo đối tượng TourImage và gán vào Tour
        List<TourImage> tourImages = new ArrayList<>();
        // (Nếu update, đoạn này có thể cần logic giữ lại ảnh cũ, ở đây mình đang tạo list mới)
        if (tour.getImages() != null) {
            tourImages.addAll(tour.getImages()); // Giữ lại ảnh cũ nếu muốn
        }

        for (String url : allImageLinks) {
            TourImage img = new TourImage();
            img.setImageUrl(url);
            img.setTour(tour);
            tourImages.add(img);
        }
        tour.setImages(tourImages);

        return tourRepository.save(tour);
    }

    @Transactional
    protected Tour updateTourInDB(Long id, TourRequest request, List<String> uploadedUrls) {
        Tour tour = getById(id); // Load lại trong session hibernate
        return saveTourToDB(tour, request, uploadedUrls); // Tái sử dụng hàm save
    }

    private List<String> uploadImagesInParallel(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        // Sử dụng parallelStream để tận dụng đa luồng CPU/IO
        return images.parallelStream().map(file -> {
            try {
                return fileUploadService.uploadFile(file);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh: " + file.getOriginalFilename(), e);
            }
        }).collect(Collectors.toList());
    }

    // Hàm map dữ liệu cơ bản (Code cũ đã làm tốt, chỉ tách ra cho gọn)
    private void mapBasicInfo(TourRequest request, Tour tour) {
        tour.setTitle(request.getTitle());
        tour.setSlug(request.getSlug());
        tour.setDuration(request.getDuration());
        tour.setPriceAdult(request.getPriceAdult());
        tour.setPriceChild(request.getPriceChild());
        tour.setDescription(request.getDescription());
        tour.setTransportation(request.getTransportation());

        if (request.getStartLocationId() != null) {
            Location startLoc = locationRepository.findById(request.getStartLocationId())
                    .orElseThrow(() -> new RuntimeException("Điểm khởi hành không tồn tại"));
            tour.setStartLocation(startLoc);
        }

        if (request.getDestinationId() != null) {
            Location destLoc = locationRepository.findById(request.getDestinationId())
                    .orElseThrow(() -> new RuntimeException("Điểm đến không tồn tại"));
            tour.setDestination(destLoc);
        }

        // Xử lý Schedules (Xóa cũ thay mới - Strategy đơn giản nhất)
        if (request.getSchedules() != null) {
            List<TourSchedule> newSchedules = new ArrayList<>();
            for (TourScheduleRequest req : request.getSchedules()) {
                TourSchedule schedule = new TourSchedule();
                schedule.setStartDate(req.getStartDate());
                schedule.setEndDate(req.getEndDate());
                schedule.setAvailableSeats(req.getAvailableSeats());
                schedule.setTour(tour);
                newSchedules.add(schedule);
            }
            if (tour.getSchedules() == null) {
                tour.setSchedules(newSchedules);
            } else {
                tour.getSchedules().clear();
                tour.getSchedules().addAll(newSchedules);
            }
        }

        // Xử lý Itineraries
        if (request.getItineraries() != null) {
            List<TourItinerary> newItineraries = new ArrayList<>();
            for (TourItineraryRequest req : request.getItineraries()) {
                TourItinerary it = new TourItinerary();
                it.setDayNumber(req.getDayNumber());
                it.setTitle(req.getTitle());
                it.setDescription(req.getDescription());
                it.setTour(tour);
                newItineraries.add(it);
            }
            if (tour.getItineraries() == null) {
                tour.setItineraries(newItineraries);
            } else {
                tour.getItineraries().clear();
                tour.getItineraries().addAll(newItineraries);
            }
        }
    }
}