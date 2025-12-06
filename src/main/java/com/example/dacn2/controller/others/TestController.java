package com.example.dacn2.controller.others;

import com.example.dacn2.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    JWTUtils jwtUtils;

    @GetMapping("/get-email")
    public ResponseEntity<?> getEmail(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("❌ Header thiếu hoặc sai định dạng Bearer Token!");
        }

        // 2. Cắt bỏ chữ "Bearer " (7 ký tự) để lấy Token thật
        String token = authHeader.substring(7);
        boolean isValid = jwtUtils.validateJwtToken(token);
        if (!isValid) {
            return ResponseEntity.badRequest().body("Token Loi");
        }
        String email = jwtUtils.getEmailFromToken(token);
        String id = jwtUtils.getUserNameFromJwtToken(token);

        return ResponseEntity.ok(email + " " + id);
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder; // Inject bộ mã hóa

    // API: Sinh mã hash từ mật khẩu gốc
    // URL: GET http://localhost:8080/api/test/hash-pass?password=123456
    @GetMapping("/hash-pass")
    public ResponseEntity<?> getHashPassword(@RequestParam("password") String password) {
        String hash = passwordEncoder.encode(password);
        return ResponseEntity.ok(hash);
    }
}
