package com.example.dacn2.Config;

import com.example.dacn2.repository.InvalidatedTokenRepository;
import com.example.dacn2.Config.CustomUserDetailsService;
import com.example.dacn2.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CustomUserDetailsService customUserDetailsService; // Service bạn vừa tạo ở Bước 1

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository; // Repository kiểm tra Blacklist

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Lấy token từ Header
            String token = getTokenFromRequest(request);

            // 2. Kiểm tra xem Token có tồn tại và Hợp lệ không
            if (token != null && jwtUtils.validateJwtToken(token)) {

                // --- KIỂM TRA BLACKLIST (LOGOUT) ---
                // Nếu token nằm trong bảng invalidated_tokens thì chặn luôn
                if (invalidatedTokenRepository.existsById(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been logged out (Blacklisted)");
                    return; // Dừng request tại đây
                }

                // 3. Lấy Email từ Token
                String email = jwtUtils.getEmailFromToken(token);

                // 4. Load thông tin User từ Database
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set thông tin request (IP, Session ID...) vào authentication
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. LƯU VÀO SECURITY CONTEXT (Bước quan trọng nhất)
                // Spring Security sẽ biết user này đã đăng nhập thành công
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Nếu có lỗi (Token sai, hết hạn...), chỉ cần log ra, không throw exception để filter chain chạy tiếp
            // (Spring Security sẽ tự xử lý lỗi 401 ở bước sau nếu endpoint yêu cầu quyền)
            logger.error("Cannot set user authentication: {}", e);
        }

        // 7. Cho phép request đi tiếp sang các Filter khác hoặc vào Controller
        filterChain.doFilter(request, response);
    }

    // Hàm phụ trợ để lấy chuỗi token sạch từ Header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ 7 ký tự đầu ("Bearer ")
        }
        return null;
    }
}