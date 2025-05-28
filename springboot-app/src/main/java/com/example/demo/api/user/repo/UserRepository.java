package com.example.demo.api.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.api.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}
