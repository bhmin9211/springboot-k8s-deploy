package com.example.demo.config.security;

import com.example.demo.config.app.SecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = UriComponentsBuilder.fromUriString(securityProperties.getFrontendBaseUrl())
                .path("/login")
                .queryParam("error", "auth_failed")
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
