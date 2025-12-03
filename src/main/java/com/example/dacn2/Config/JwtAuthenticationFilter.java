package com.example.dacn2.Config;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.repository.auth.InvalidatedTokenRepository;
import com.example.dacn2.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper; // Import thư viện JSON
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy token
            String token = getTokenFromRequest(request);

            if (token != null) {
                // 2. Validate Token (Hàm này giờ sẽ ném Exception nếu lỗi)
                jwtUtils.validateJwtToken(token);

                // 3. Check Blacklist
                if (invalidatedTokenRepository.existsById(token)) {
                    // Tự ném lỗi để xuống catch xử lý chung
                    throw new RuntimeException("Token đã đăng xuất (Blacklisted)");
                }

                // 4. Lấy thông tin User
                String email = jwtUtils.getEmailFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. Set Authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Nếu mọi thứ êm đẹp, cho đi tiếp
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // Bắt lỗi Token hết hạn
            sendErrorResponse(response, 1001, "Token đã hết hạn (Expired)");
        } catch (SignatureException | MalformedJwtException e) {
            // Bắt lỗi Token sai chữ ký hoặc sai định dạng
            sendErrorResponse(response, 1002, "Token không hợp lệ (Invalid Signature/Format)");
        } catch (Exception e) {
            // Bắt các lỗi còn lại (Ví dụ: Blacklist, User không tìm thấy...)
            sendErrorResponse(response, 9999, "Lỗi xác thực: " + e.getMessage());
        }
    }

    // Hàm phụ trợ để viết JSON trả về Client ngay lập tức
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Trả về 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Tạo đối tượng ApiResponse chuẩn của bạn
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(code)
                .message(message)
                .build();

        // Dùng Jackson để biến Object thành chuỗi JSON và ghi vào response
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        // Quan trọng: Không gọi filterChain.doFilter() nữa để chặn request tại đây
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}