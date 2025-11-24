package com.example.dacn2.service.UserService.Auth;

import com.example.dacn2.dto.request.LoginRequest;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.entity.Account;
import com.example.dacn2.repository.AccountRepositoryInterface;
import com.example.dacn2.service.UserServiceInterface.Auth.LoginServiceInterface;
import com.example.dacn2.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginServiceInterface {
    @Autowired
    AccountRepositoryInterface accountRepositoryInterface;

    @Autowired
    JWTUtils jwtUtils;

    @Override
    public LoginReponse login(LoginRequest request) {


        String cleanEmail = request.getEmail().trim();

        // 3. Tìm kiếm
        Account account = accountRepositoryInterface.findByEmail(cleanEmail)
                .orElseThrow(() -> {
                    // In ra log nếu không tìm thấy
                    System.out.println("LỖI: Không tìm thấy email [" + cleanEmail + "] trong Database!");
                    return new RuntimeException("Email not exist");
                });


        // 4. Kiểm tra Password
        if (!account.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Mat khau khong dung");
        }

        // 5. Kiểm tra Status (Thêm check null để tránh lỗi NullPointerException)
        if ("BANNED".equals(account.getStatus())) {
            throw new RuntimeException("Tai khoan da bi cam");
        }

        String token = jwtUtils.generaToken(account);

        // 6. Map dữ liệu trả về
        LoginReponse response = new LoginReponse();
        response.setId(account.getId());
        response.setEmail(account.getEmail());
        response.setRole(account.getRole());
        response.setToken(token);

        return response;
    }
}
