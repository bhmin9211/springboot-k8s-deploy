package com.example.demo.api.auth.adapter.out;

import com.example.demo.api.auth.port.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 비밀번호 인코더 어댑터 (헥사고날 아키텍처의 아웃바운드 어댑터)
 * 실제 비밀번호 암호화를 담당하는 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public String encode(String rawPassword) {
        log.debug("비밀번호 암호화");
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            log.error("비밀번호 암호화 중 오류 발생: ", e);
            throw e;
        }
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        log.debug("비밀번호 일치 여부 확인");
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.error("비밀번호 일치 여부 확인 중 오류 발생: ", e);
            return false;
        }
    }
} 