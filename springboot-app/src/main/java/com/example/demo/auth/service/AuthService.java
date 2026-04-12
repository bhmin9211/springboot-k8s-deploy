package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.dto.RegisterResponse;
import com.example.demo.config.provider.JwtTokenProvider;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        log.info("사용자 {} 로그인 시도", request.getUsername());

        try {
            UserEntity user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user == null) {
                return LoginResponse.builder()
                        .message("존재하지 않는 사용자입니다.")
                        .build();
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return LoginResponse.builder()
                        .message("비밀번호가 일치하지 않습니다.")
                        .build();
            }

            return LoginResponse.builder()
                    .token(jwtTokenProvider.createToken(user.getUsername(), user.getRole()))
                    .username(user.getUsername())
                    .role(user.getRole())
                    .message("로그인 성공")
                    .build();
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            return LoginResponse.builder()
                    .message("로그인 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        log.info("사용자 {} 회원가입 시도", request.getUsername());

        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return RegisterResponse.builder()
                        .username(request.getUsername())
                        .message("이미 존재하는 사용자명입니다.")
                        .success(false)
                        .build();
            }

            UserEntity savedUser = userRepository.save(UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role("USER")
                    .build());

            return RegisterResponse.builder()
                    .username(savedUser.getUsername())
                    .message("회원가입이 완료되었습니다.")
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .message("회원가입 처리 중 오류가 발생했습니다.")
                    .success(false)
                    .build();
        }
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsername(token);
    }
}
