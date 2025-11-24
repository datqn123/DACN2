package com.example.dacn2.utils;

import com.example.dacn2.entity.Account;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generaToken(Account account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", account.getEmail());
        claims.put("role", account.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(account.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.println("LỖI: Chữ ký Token không hợp lệ (Token giả).");
        } catch (MalformedJwtException e) {
            System.out.println("LỖI: Chuỗi Token sai định dạng (Thiếu ký tự, thừa khoảng trắng...).");
        } catch (ExpiredJwtException e) {
            System.out.println("LỖI: Token đã hết hạn sử dụng.");
        } catch (UnsupportedJwtException e) {
            System.out.println("LỖI: Token không được hỗ trợ.");
        } catch (IllegalArgumentException e) {
            System.out.println("LỖI: Chuỗi claims rỗng.");
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }
}
