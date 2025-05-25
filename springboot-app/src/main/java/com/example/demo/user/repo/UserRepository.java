package com.example.demo.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
