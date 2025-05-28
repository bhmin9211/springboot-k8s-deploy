package com.example.demo.api.auth.ctr;

import com.example.demo.api.auth.model.LoginRequestVo;
import com.example.demo.api.auth.model.RegisterRequestVo;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestVo request) {
        UserEntity user = userService.findByUsername(String.valueOf(request.getUsername()));

        if (!passwordEncoder.matches(String.valueOf(request.getPassword()), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestVo request) {

        if (userService.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 사용자입니다.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userService.save(user);
        return ResponseEntity.ok("회원가입 완료");
    }
}
