package com.example.dacn2.Config;

import com.example.dacn2.entity.User.Account;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepositoryInterface accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm Account trong DB
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Chuyển đổi Role & Permission của mình thành Authority của Spring
        // Ví dụ: ROLE_ADMIN, HOTEL_CREATE...
        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // Thêm prefix ROLE_ cho chuẩn Spring
                .collect(Collectors.toList());

        // Nếu muốn thêm cả Permission vào Authority (để dùng @PreAuthorize("hasAuthority('HOTEL_CREATE')"))
        // Bạn có thể lặp tiếp qua role.getPermissions() để add vào list trên.

        // 3. Trả về đối tượng User chuẩn của Spring Security
        return new User(
                account.getEmail(),
                account.getPassword(),
                authorities
        );
    }
}