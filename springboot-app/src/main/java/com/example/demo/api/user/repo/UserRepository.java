package com.example.demo.api.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.api.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
