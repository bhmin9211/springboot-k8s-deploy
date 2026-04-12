package com.example.demo.health.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    public boolean healthcheck() {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
