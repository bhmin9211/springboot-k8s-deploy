package com.example.demo.api.auth.adapter.out;

import com.example.demo.api.auth.port.out.UserRepositoryPort;
import com.example.demo.api.user.entity.UserEntity;
import com.example.demo.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 사용자 저장소 어댑터 (헥사고날 아키텍처의 아웃바운드 어댑터)
 * 실제 데이터베이스와 통신하는 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserRepository userRepository;
    
    @Override
    public UserEntity findByUsername(String username) {
        log.debug("사용자명 {}으로 사용자 조회", username);
        try {
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생: ", e);
            return null;
        }
    }
    
    @Override
    public UserEntity save(UserEntity user) {
        log.debug("사용자 저장: {}", user.getUsername());
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("사용자 저장 중 오류 발생: ", e);
            throw e;
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        log.debug("사용자명 {} 존재 여부 확인", username);
        try {
            return userRepository.findByUsername(username).isPresent();
        } catch (Exception e) {
            log.error("사용자명 존재 여부 확인 중 오류 발생: ", e);
            return false;
        }
    }
} 