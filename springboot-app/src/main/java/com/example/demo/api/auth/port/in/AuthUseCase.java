package com.example.demo.api.auth.port.in;

import com.example.demo.api.auth.model.LoginRequest;
import com.example.demo.api.auth.model.LoginResponse;
import com.example.demo.api.auth.model.RegisterRequest;
import com.example.demo.api.auth.model.RegisterResponse;

/**
 * 인증 유스케이스 포트 (헥사고날 아키텍처의 인바운드 포트)
 * 애플리케이션의 핵심 비즈니스 로직을 정의
 */
public interface AuthUseCase {
    
    /**
     * 사용자 로그인
     * @param request 로그인 요청 정보
     * @return 로그인 응답 정보
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 사용자 회원가입
     * @param request 회원가입 요청 정보
     * @return 회원가입 응답 정보
     */
    RegisterResponse register(RegisterRequest request);
    
    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효성 검증 결과
     */
    boolean validateToken(String token);
    
    /**
     * 토큰에서 사용자명 추출
     * @param token JWT 토큰
     * @return 사용자명
     */
    String getUsernameFromToken(String token);
} 