package com.example.demo.api.auth.adapter.in;

import com.example.demo.api.auth.model.LoginRequest;
import com.example.demo.api.auth.model.LoginResponse;
import com.example.demo.api.auth.model.RegisterRequest;
import com.example.demo.api.auth.model.RegisterResponse;
import com.example.demo.api.auth.port.in.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 컨트롤러 (헥사고날 아키텍처의 인바운드 어댑터)
 * HTTP 요청을 받아서 포트를 통해 핵심 비즈니스 로직을 호출
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthUseCase authUseCase;
    
    /**
     * 사용자 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청: {}", request.getUsername());
        
        LoginResponse response = authUseCase.login(request);
        
        if (response.getToken() != null) {
            // 로그인 성공
            Map<String, Object> result = Map.of(
                "token", response.getToken(),
                "username", response.getUsername(),
                "role", response.getRole(),
                "message", response.getMessage()
            );
            return ResponseEntity.ok(result);
        } else {
            // 로그인 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", response.getMessage()));
        }
    }
    
    /**
     * 사용자 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("회원가입 요청: {}", request.getUsername());
        
        RegisterResponse response = authUseCase.register(request);
        
        if (response.isSuccess()) {
            // 회원가입 성공
            return ResponseEntity.ok(Map.of(
                "username", response.getUsername(),
                "message", response.getMessage()
            ));
        } else {
            // 회원가입 실패
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", response.getMessage()));
        }
    }
    
    /**
     * 토큰 유효성 검증
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorization) {
        log.info("토큰 유효성 검증 요청");
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰 형식입니다."));
        }
        
        String token = authorization.substring(7);
        boolean isValid = authUseCase.validateToken(token);
        
        if (isValid) {
            String username = authUseCase.getUsernameFromToken(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "message", "토큰이 유효합니다."
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "토큰이 유효하지 않습니다."));
        }
    }
    
    /**
     * 토큰에서 사용자 정보 추출
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        log.info("현재 사용자 정보 요청");
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰 형식입니다."));
        }
        
        String token = authorization.substring(7);
        boolean isValid = authUseCase.validateToken(token);
        
        if (isValid) {
            String username = authUseCase.getUsernameFromToken(token);
            return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "사용자 정보 조회 성공"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "토큰이 유효하지 않습니다."));
        }
    }
    
    /**
     * 헬스체크
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("인증 서비스 헬스체크 요청");
        return ResponseEntity.ok("인증 서비스가 정상적으로 동작하고 있습니다.");
    }
} 