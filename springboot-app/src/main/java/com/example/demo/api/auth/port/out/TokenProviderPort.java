package com.example.demo.api.auth.port.out;

/**
 * 토큰 제공자 포트 (헥사고날 아키텍처의 아웃바운드 포트)
 * JWT 토큰 관련 외부 시스템과의 인터페이스를 정의
 */
public interface TokenProviderPort {
    
    /**
     * JWT 토큰 생성
     * @param username 사용자명
     * @param role 사용자 역할
     * @return 생성된 JWT 토큰
     */
    String createToken(String username, String role);
    
    /**
     * JWT 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효성 검증 결과
     */
    boolean validateToken(String token);
    
    /**
     * JWT 토큰에서 사용자명 추출
     * @param token JWT 토큰
     * @return 사용자명
     */
    String getUsernameFromToken(String token);
    
    /**
     * JWT 토큰에서 역할 추출
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    String getRoleFromToken(String token);
} 