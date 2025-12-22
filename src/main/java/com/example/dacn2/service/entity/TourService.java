package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.tour.*;
import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.dto.response.home.TourSearchResponse;
import com.example.dacn2.entity.*;
import com.example.dacn2.entity.tour.*;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import com.example.dacn2.repository.tour.TourRepository;
import com.example.dacn2.repository.tour.TourSpecification;
import com.example.dacn2.service.user_service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TourService {

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private LocationInterfaceRepository locationRepository;
    @Autowired
    private FileUploadService fileUploadService;

    public List<Tour> getAll() {
        return tourRepository.findAll();
    }

    public Page<TourCardResponse> getAllPaged(Pageable pageable) {
        return tourRepository.findAll(pageable).map(this::mapToTourCard);
    }

    private TourCardResponse mapToTourCard(Tour tour) {
        return TourCardResponse.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .slug(tour.getSlug())
                .duration(tour.getDuration())
                .startLocationName(tour.getStartLocation() != null ? tour.getStartLocation().getName() : null)
                .destinationName(tour.getDestination() != null ? tour.getDestination().getName() : null)
                .thumbnail(tour.getThumbnail())
                .price(tour.getPrice())
                .transportation(tour.getTransportation())
                .build();
    }

    private static final int PAGE_SIZE = 20;

    public TourSearchResponse searchToursWithFilter(TourFilterRequest filter) {
        // Tạo Pageable với sort theo giá nếu có yêu cầu
        int page = filter.getPage() != null ? filter.getPage() : 0;
        Sort sort = Sort.unsorted();
        if ("ASC".equalsIgnoreCase(filter.getSortByPrice())) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("DESC".equalsIgnoreCase(filter.getSortByPrice())) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        }
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

        // Query database với pagination - CHỈ LẤY 20 BẢN GHI CẦN THIẾT
        Page<Tour> tourPage = tourRepository.findAll(TourSpecification.withFilters(filter), pageable);

        // Convert sang DTO
        List<TourCardResponse> tourCards = tourPage.getContent().stream()
                .map(this::mapToTourCard)
                .toList();

        return TourSearchResponse.builder()
                .tours(tourCards)
                .currentPage(tourPage.getNumber())
                .totalPages(tourPage.getTotalPages())
                .totalElements(tourPage.getTotalElements())
                .pageSize(tourPage.getSize())
                .hasNext(tourPage.hasNext())
                .hasPrevious(tourPage.hasPrevious())
                .build();
    }

    private List<TourCardResponse> sortByPrice(List<TourCardResponse> results, String sortByPrice) {
        if (sortByPrice == null) {
            return results;
        }
        Comparator<TourCardResponse> comparator;
        if ("ASC".equalsIgnoreCase(sortByPrice)) {
            comparator = (t1, t2) -> {
                Double p1 = t1.getPrice() != null ? t1.getPrice() : Double.MAX_VALUE;
                Double p2 = t2.getPrice() != null ? t2.getPrice() : Double.MAX_VALUE;
                return p1.compareTo(p2);
            };
        } else if ("DESC".equalsIgnoreCase(sortByPrice)) {
            // Giá cao đến thấp (null giá đẩy xuống cuối)
            comparator = (t1, t2) -> {
                Double p1 = t1.getPrice() != null ? t1.getPrice() : 0.0;
                Double p2 = t2.getPrice() != null ? t2.getPrice() : 0.0;
                return p2.compareTo(p1);
            };
        } else {
            return results;
        }

        return results.stream().sorted(comparator).toList();
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

    @Transactional
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

        // 2. Xử lý ảnh
        // Tổng hợp tất cả URL ảnh đích (gồm ảnh cũ giữ lại + ảnh mới upload)
        List<String> targetUrls = new ArrayList<>();
        if (request.getImageUrls() != null) {
            targetUrls.addAll(request.getImageUrls());
        }
        if (uploadedUrls != null) {
            targetUrls.addAll(uploadedUrls);
        }

        // Khởi tạo danh sách ảnh của Tour nếu chưa có
        if (tour.getImages() == null) {
            tour.setImages(new ArrayList<>());
        }

        // a. XÓA những ảnh không còn nằm trong danh sách đích
        // (Nhờ orphanRemoval = true bên Entity, việc remove khỏi list sẽ xóa DB)
        tour.getImages().removeIf(img -> !targetUrls.contains(img.getImageUrl()));

        // b. THÊM những ảnh mới chưa có trong danh sách hiện tại
        List<String> currentUrls = tour.getImages().stream()
                .map(TourImage::getImageUrl)
                .toList();

        for (String url : targetUrls) {
            if (!currentUrls.contains(url)) {
                TourImage img = new TourImage();
                img.setImageUrl(url);
                img.setTour(tour);
                tour.getImages().add(img);
            }
        }

        // 3. Cập nhật Thumbnail (Lấy cái đầu tiên làm thumbnail nếu có)
        if (!targetUrls.isEmpty()) {
            tour.setThumbnail(targetUrls.get(0));
        } else {
            tour.setThumbnail(null);
        }

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
        tour.setPrice(request.getPrice());
        tour.setOriginalPrice(request.getOriginalPrice());
        tour.setMaxPeople(request.getMaxPeople());
        tour.setDescription(request.getDescription());
        tour.setTransportation(request.getTransportation());

        // Điểm nổi bật, Bao gồm, Không bao gồm (Chuyển List -> JSON String)
        if (request.getHighlights() != null) {
            tour.setHighlights(String.join("||", request.getHighlights()));
        }
        if (request.getIncludes() != null) {
            tour.setIncludes(String.join("||", request.getIncludes()));
        }
        if (request.getExcludes() != null) {
            tour.setExcludes(String.join("||", request.getExcludes()));
        }

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