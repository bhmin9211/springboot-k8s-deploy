package com.example.demo.api.healthcheck.svc;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final EntityManager entityManager;

    public boolean healthcheck() {
        try {
            entityManager.createQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
