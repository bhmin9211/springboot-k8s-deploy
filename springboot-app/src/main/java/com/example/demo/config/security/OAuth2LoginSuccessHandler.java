package com.example.demo.config.security;

import com.example.demo.config.app.SecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String POST_LOGIN_REDIRECT_ATTRIBUTE = "POST_LOGIN_REDIRECT_ATTRIBUTE";

    private final SecurityProperties securityProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        String redirectPath = "/";

        if (session != null) {
            Object savedRedirect = session.getAttribute(POST_LOGIN_REDIRECT_ATTRIBUTE);
            if (savedRedirect instanceof String savedValue && !savedValue.isBlank()) {
                redirectPath = savedValue.startsWith("/") ? savedValue : "/" + savedValue;
            }
            session.removeAttribute(POST_LOGIN_REDIRECT_ATTRIBUTE);
        }

        String targetUrl = UriComponentsBuilder.fromUriString(securityProperties.getFrontendBaseUrl())
                .path(redirectPath)
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
