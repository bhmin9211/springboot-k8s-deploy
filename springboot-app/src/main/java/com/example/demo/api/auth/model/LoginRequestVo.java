package com.example.demo.api.auth.model;

import lombok.Data;

@Data
public class LoginRequestVo {
    private String username;
    private String password;
}