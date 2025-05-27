package com.example.demo.api.user.svc;

import com.example.demo.api.user.entity.UserEntity;
import com.example.demo.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 유저 조회
     */
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
     /*           .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));*/
    }

    /**
     * 회원 저장 (회원가입 시 사용 가능)
     */
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}
