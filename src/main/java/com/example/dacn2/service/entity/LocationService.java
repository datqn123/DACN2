package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.location.LocationRequest;
import com.example.dacn2.entity.Location;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationInterfaceRepository locationRepository;

    // 1. Lấy danh sách tất cả (Cho Admin)
    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    // 2. Lấy chi tiết theo ID
    public Location getById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm với ID: " + id));
    }

    // 3. Tạo mới
    @Transactional
    public Location create(LocationRequest request) {
        //  check exist
        if(locationRepository.existsByName(request.getName())){
            throw new RuntimeException("Location is exist");
        }
        // Check trùng slug
        if (locationRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Slug '" + request.getSlug() + "' đã tồn tại!");
        }

        Location location = new Location();
        mapRequestToEntity(request, location);

        return locationRepository.save(location);
    }

    // 4. Cập nhật
    @Transactional
    public Location update(Long id, LocationRequest request) {
        Location location = getById(id);

        // Nếu đổi slug thì phải check xem slug mới có trùng với ai khác không
        if (!location.getSlug().equals(request.getSlug()) && locationRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Slug '" + request.getSlug() + "' đã tồn tại!");
        }

        mapRequestToEntity(request, location);

        return locationRepository.save(location);
    }

    // 5. Xóa
    @Transactional
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy địa điểm để xóa");
        }
        locationRepository.deleteById(id);
    }

    // Hàm phụ trợ: Map dữ liệu từ DTO sang Entity
    private void mapRequestToEntity(LocationRequest request, Location location) {
        location.setName(request.getName());
        location.setSlug(request.getSlug());
        location.setDescription(request.getDescription());
        location.setThumbnail(request.getThumbnail());
        location.setType(request.getType());

        // Xử lý quan hệ cha-con
        if (request.getParentId() != null) {
            Location parent = locationRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent Location (Cha) không tồn tại"));
            location.setParent(parent);
        } else {
            location.setParent(null); // Nếu không gửi parentId thì là cấp cao nhất
        }
    }


}