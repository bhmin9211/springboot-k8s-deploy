package com.example.demo.api.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 회원가입 응답 모델 (Builder 패턴 적용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private String username;
    private String message;
    private boolean success;
} 