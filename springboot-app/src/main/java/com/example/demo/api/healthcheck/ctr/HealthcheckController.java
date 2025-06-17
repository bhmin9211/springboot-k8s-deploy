package com.example.demo.api.healthcheck.ctr;

import com.example.demo.api.healthcheck.svc.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthcheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthCheck() {
        if (healthCheckService.healthcheck()) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(503).body("JPA/DB DOWN");
        }
    }

    @GetMapping("/message")
    public String message() {
        return "Hello from Spring Boot!";
    }
}
