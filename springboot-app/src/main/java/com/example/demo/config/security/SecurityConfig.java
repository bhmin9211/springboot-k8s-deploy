package com.example.demo.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new LinkedHashSet<>();

            for (GrantedAuthority authority : authorities) {
                mappedAuthorities.add(authority);

                if (authority instanceof OidcUserAuthority oidcAuthority) {
                    Map<String, Object> claims = oidcAuthority.getUserInfo() != null
                            ? oidcAuthority.getUserInfo().getClaims()
                            : oidcAuthority.getIdToken().getClaims();
                    mappedAuthorities.addAll(extractKeycloakRoles(claims));
                }
            }

            return mappedAuthorities;
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login/**", "/auth/health", "/health/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/error").permitAll()
                        .requestMatchers("/auth/me", "/auth/status", "/auth/logout").authenticated()
                        .requestMatchers("/k8s/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(grantedAuthoritiesMapper()))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/k8s/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/auth/me")
                        )
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/auth/status")
                        )
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/auth/logout")
                        )
                )
                .build();
    }

    private Collection<GrantedAuthority> extractKeycloakRoles(Map<String, Object> claims) {
        Set<String> roles = new LinkedHashSet<>();
        extractRolesFromRealmAccess(claims, roles);
        extractRolesFromResourceAccess(claims, roles);

        return roles.stream()
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(String::toUpperCase)
                .map(SecurityRoles::asAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SuppressWarnings("unchecked")
    private void extractRolesFromRealmAccess(Map<String, Object> claims, Set<String> roles) {
        Object realmAccess = claims.get("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
            return;
        }

        Object realmRoles = realmAccessMap.get("roles");
        if (realmRoles instanceof Collection<?> roleValues) {
            roleValues.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(roles::add);
        }
    }

    @SuppressWarnings("unchecked")
    private void extractRolesFromResourceAccess(Map<String, Object> claims, Set<String> roles) {
        Object resourceAccess = claims.get("resource_access");
        if (!(resourceAccess instanceof Map<?, ?> resourceAccessMap)) {
            return;
        }

        resourceAccessMap.values().stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(access -> access.get("roles"))
                .filter(Collection.class::isInstance)
                .map(Collection.class::cast)
                .forEach(roleValues -> ((Collection<?>) roleValues).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(roles::add));
    }
}
