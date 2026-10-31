package com.example.demo.health.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public boolean healthcheck() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (RuntimeException exception) {
            log.warn("DB health check failed", exception);
            return false;
        }
    }
}
