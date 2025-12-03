package com.example.dacn2.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        // Upload file lên Cloudinary
        // "folder", "dacn2_images" -> Tự động tạo thư mục tên dacn2_images trên cloud để quản lý cho gọn
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "dacn2_images"));

        // Trả về đường dẫn URL công khai của ảnh
        return uploadResult.get("url").toString();
    }
}