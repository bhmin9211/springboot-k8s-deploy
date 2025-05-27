package com.example.demo.config.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private String secretKey = "this-is-my-very-secret-jwt-key-2024"; // 최소 32자 이상 권장
    private final long validityInMilliseconds = 3600000; // 1시간

    private Key key;

    @PostConstruct
    protected void init() {
        // 문자열을 Key 객체로 안전하게 변환 (Base64 불필요)
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String username = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();

        return new UsernamePasswordAuthenticationToken(username, "", List.of());
    }
}
