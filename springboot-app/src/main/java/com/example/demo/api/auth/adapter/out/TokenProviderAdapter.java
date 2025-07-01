package com.example.demo.api.auth.adapter.out;

import com.example.demo.api.auth.port.out.TokenProviderPort;
import com.example.demo.config.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 토큰 제공자 어댑터 (헥사고날 아키텍처의 아웃바운드 어댑터)
 * 실제 JWT 토큰 처리를 담당하는 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProviderAdapter implements TokenProviderPort {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public String createToken(String username, String role) {
        log.debug("사용자 {}에 대한 JWT 토큰 생성", username);
        try {
            return jwtTokenProvider.createToken(username, role);
        } catch (Exception e) {
            log.error("JWT 토큰 생성 중 오류 발생: ", e);
            throw e;
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        log.debug("JWT 토큰 유효성 검증");
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            log.error("JWT 토큰 유효성 검증 중 오류 발생: ", e);
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        log.debug("JWT 토큰에서 사용자명 추출");
        try {
            return jwtTokenProvider.getUsername(token);
        } catch (Exception e) {
            log.error("JWT 토큰에서 사용자명 추출 중 오류 발생: ", e);
            return null;
        }
    }
    
    @Override
    public String getRoleFromToken(String token) {
        log.debug("JWT 토큰에서 역할 추출");
        try {
            return jwtTokenProvider.getRole(token);
        } catch (Exception e) {
            log.error("JWT 토큰에서 역할 추출 중 오류 발생: ", e);
            return null;
        }
    }
} 