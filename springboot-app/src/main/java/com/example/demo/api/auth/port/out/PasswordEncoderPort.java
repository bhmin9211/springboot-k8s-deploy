package com.example.demo.api.auth.port.out;

/**
 * 비밀번호 인코더 포트 (헥사고날 아키텍처의 아웃바운드 포트)
 * 비밀번호 암호화 관련 외부 시스템과의 인터페이스를 정의
 */
public interface PasswordEncoderPort {
    
    /**
     * 비밀번호 암호화
     * @param rawPassword 원본 비밀번호
     * @return 암호화된 비밀번호
     */
    String encode(String rawPassword);
    
    /**
     * 비밀번호 일치 여부 확인
     * @param rawPassword 원본 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @return 일치 여부
     */
    boolean matches(String rawPassword, String encodedPassword);
} 