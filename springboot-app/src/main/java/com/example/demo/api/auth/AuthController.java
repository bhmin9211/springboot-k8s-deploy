package com.example.demo.api.auth;

import com.example.demo.api.user.entity.UserEntity;
import com.example.demo.api.user.svc.UserService;
import com.example.demo.config.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map request) {
        UserEntity user = userService.findByUsername(String.valueOf(request.get("username")));

        if (!passwordEncoder.matches(String.valueOf(request.get("password")), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
