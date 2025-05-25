package com.example.demo.root;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.svc.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RootController {


    private final UserService userService;

    @GetMapping("/")
    public String hello() {
        return "OK";
    }

    @GetMapping("/user")
    public UserEntity user() {
        return userService.findByUsername("minbh");
    }
}
