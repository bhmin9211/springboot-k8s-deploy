package com.example.demo.config.security;

import java.util.List;
import java.util.stream.Collectors;

public final class SecurityRoles {

    public static final String VIEWER = "VIEWER";
    public static final String OPERATOR = "OPERATOR";
    public static final String ADMIN = "ADMIN";

    private SecurityRoles() {
    }

    public static String asAuthority(String role) {
        return "ROLE_" + role;
    }

    public static List<String> normalizeRoleAuthorities(List<String> authorities) {
        return authorities.stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .sorted()
                .collect(Collectors.toList());
    }
}
