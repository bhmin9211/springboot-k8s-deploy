package com.example.demo.auth.controller;

import com.example.demo.config.app.SecurityProperties;
import com.example.demo.config.security.OAuth2LoginSuccessHandler;
import com.example.demo.config.security.SecurityRoles;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityProperties securityProperties;

    @GetMapping("/login/keycloak")
    public void loginWithKeycloak(@RequestParam(name = "redirect", defaultValue = "/") String redirect,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        session.setAttribute(OAuth2LoginSuccessHandler.POST_LOGIN_REDIRECT_ATTRIBUTE, sanitizeRedirect(redirect));

        String authorizationPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorization/keycloak")
                .build()
                .toUriString();

        response.sendRedirect(authorizationPath);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OidcUser oidcUser,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "code", "AUTH_UNAUTHORIZED",
                            "message", "Authentication is required."
                    ));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        List<String> roles = SecurityRoles.normalizeRoleAuthorities(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        response.put("username", firstNonBlank(oidcUser.getPreferredUsername(), oidcUser.getSubject()));
        response.put("email", oidcUser.getEmail());
        response.put("name", oidcUser.getFullName());
        response.put("roles", roles);
        response.put("access", Map.of(
                "canView", !roles.isEmpty(),
                "canOperate", roles.contains(SecurityRoles.OPERATOR) || roles.contains(SecurityRoles.ADMIN),
                "canAdmin", roles.contains(SecurityRoles.ADMIN)
        ));
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(@AuthenticationPrincipal OidcUser oidcUser,
                                    Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        List<String> roles = SecurityRoles.normalizeRoleAuthorities(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", firstNonBlank(oidcUser.getPreferredUsername(), oidcUser.getSubject()),
                "roles", roles
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "Signed out successfully.",
                "frontendBaseUrl", securityProperties.getFrontendBaseUrl()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("인증 서비스가 정상적으로 동작하고 있습니다.");
    }

    private String sanitizeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank() || !redirect.startsWith("/")) {
            return "/";
        }
        return redirect;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
