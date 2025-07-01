package com.example.demo.api.auth.service;

import com.example.demo.api.auth.model.LoginRequest;
import com.example.demo.api.auth.model.LoginResponse;
import com.example.demo.api.auth.model.RegisterRequest;
import com.example.demo.api.auth.model.RegisterResponse;
import com.example.demo.api.auth.port.in.AuthUseCase;
import com.example.demo.api.auth.port.out.PasswordEncoderPort;
import com.example.demo.api.auth.port.out.TokenProviderPort;
import com.example.demo.api.auth.port.out.UserRepositoryPort;
import com.example.demo.api.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 인증 서비스 (헥사고날 아키텍처의 핵심 비즈니스 로직)
 * 인바운드 포트를 구현하고 아웃바운드 포트를 통해 외부 시스템과 통신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthUseCase {
    
    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    private final PasswordEncoderPort passwordEncoder;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("사용자 {} 로그인 시도", request.getUsername());
        
        try {
            // 사용자 조회
            UserEntity user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                log.warn("존재하지 않는 사용자: {}", request.getUsername());
                return LoginResponse.builder()
                        .message("존재하지 않는 사용자입니다.")
                        .build();
            }
            
            // 비밀번호 검증
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("비밀번호 불일치: {}", request.getUsername());
                return LoginResponse.builder()
                        .message("비밀번호가 일치하지 않습니다.")
                        .build();
            }
            
            // JWT 토큰 생성
            String token = tokenProvider.createToken(user.getUsername(), user.getRole());
            
            log.info("사용자 {} 로그인 성공", request.getUsername());
            
            return LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .message("로그인 성공")
                    .build();
                    
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: ", e);
            return LoginResponse.builder()
                    .message("로그인 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }
    
    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("사용자 {} 회원가입 시도", request.getUsername());
        
        try {
            // 사용자명 중복 확인
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("이미 존재하는 사용자명: {}", request.getUsername());
                return RegisterResponse.builder()
                        .username(request.getUsername())
                        .message("이미 존재하는 사용자명입니다.")
                        .success(false)
                        .build();
            }
            
            // 새 사용자 엔티티 생성
            UserEntity user = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role("USER")
                    .build();
            
            // 사용자 저장
            UserEntity savedUser = userRepository.save(user);
            
            log.info("사용자 {} 회원가입 성공", request.getUsername());
            
            return RegisterResponse.builder()
                    .username(savedUser.getUsername())
                    .message("회원가입이 완료되었습니다.")
                    .success(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생: ", e);
            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .message("회원가입 처리 중 오류가 발생했습니다.")
                    .success(false)
                    .build();
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        log.debug("토큰 유효성 검증");
        try {
            return tokenProvider.validateToken(token);
        } catch (Exception e) {
            log.error("토큰 유효성 검증 중 오류 발생: ", e);
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        log.debug("토큰에서 사용자명 추출");
        try {
            return tokenProvider.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("토큰에서 사용자명 추출 중 오류 발생: ", e);
            return null;
        }
    }
} 