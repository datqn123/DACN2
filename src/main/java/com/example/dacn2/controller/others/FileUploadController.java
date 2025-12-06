package com.example.dacn2.controller.others;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')") // Chỉ admin mới được up ảnh
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.uploadFile(file);
            return ApiResponse.<String>builder()
                    .result(imageUrl) // Trả về link ảnh: "https://res.cloudinary..."
                    .message("Upload ảnh thành công")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
        }
    }
}