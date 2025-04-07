package com.example.demo.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/message")
    public String message() {
        return "Hello from Spring Boot!";
    }
}
