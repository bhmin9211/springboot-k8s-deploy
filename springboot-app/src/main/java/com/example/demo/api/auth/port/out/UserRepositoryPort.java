package com.example.demo.api.auth.port.out;

import com.example.demo.api.user.entity.UserEntity;

/**
 * 사용자 저장소 포트 (헥사고날 아키텍처의 아웃바운드 포트)
 * 외부 시스템과의 인터페이스를 정의
 */
public interface UserRepositoryPort {
    
    /**
     * 사용자명으로 사용자 조회
     * @param username 사용자명
     * @return 사용자 엔티티
     */
    UserEntity findByUsername(String username);
    
    /**
     * 사용자 저장
     * @param user 사용자 엔티티
     * @return 저장된 사용자 엔티티
     */
    UserEntity save(UserEntity user);
    
    /**
     * 사용자명 존재 여부 확인
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
} 