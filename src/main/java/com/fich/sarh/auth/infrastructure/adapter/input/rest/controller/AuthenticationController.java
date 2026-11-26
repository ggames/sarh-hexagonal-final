package com.fich.sarh.auth.infrastructure.adapter.input.rest.controller;

import com.fich.sarh.auth.application.ports.entrypoint.api.AuthApiPort;
import com.fich.sarh.auth.application.ports.entrypoint.api.UserResetPasswordApiPort;
import com.fich.sarh.auth.infrastructure.adapter.configuration.security.CustomUserDetails;
import com.fich.sarh.auth.application.model.LoginRequest;
import com.fich.sarh.auth.infrastructure.adapter.input.rest.model.request.UserChangePasswordRequest;
import com.fich.sarh.auth.application.model.AuthResponse;
import com.fich.sarh.common.exceptions.BusinessRuleViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String COOKIE_PATH = "/auth";

    private final AuthApiPort authApiPort;

    private final UserResetPasswordApiPort passwordService;

    @Value("${jwt.time.refreshExpiration:604800000}")
    private long refreshTokenMillis = 604_800_000L;

    @Value("${auth.refresh.cookie.secure:true}")
    private boolean secureCookie = true;

    @Value("${auth.refresh.cookie.same-site:Lax}")
    private String sameSite = "Lax";

    @PostMapping(value = "log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest userRequest) {

        AuthResponse authResponse = authApiPort.login(userRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(authResponse.getRefreshToken()).toString())
                .body(authResponse);

    }

    @PostMapping("refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("No hay una sesión activa");
        }

        AuthResponse authResponse = authApiPort.refreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(authResponse.getRefreshToken()).toString())
                .body(authResponse);

    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {

        authApiPort.logout(refreshToken);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookie)
                .path(COOKIE_PATH)
                .sameSite(sameSite)
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();

    }

    @GetMapping("csrf")
    public ResponseEntity<Void> csrf(HttpServletRequest request) {

        Object token = request.getAttribute(CsrfToken.class.getName());

        if (token instanceof CsrfToken csrfToken) {
            return ResponseEntity.noContent()
                    .header("X-XSRF-TOKEN", csrfToken.getToken())
                    .build();
        }

        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/change")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody @Valid UserChangePasswordRequest request) {

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BusinessRuleViolationException("Usuario no autenticado");
        }

        boolean success = passwordService.changePassword(
                user.getId(), request.currentPassword(), request.newPassword()
        );

        if (!success) {
            throw new BusinessRuleViolationException("Password actual incorrecta");
        }

        return ResponseEntity.ok(Map.of("message", "Password actualizada correctamente"));

    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .path(COOKIE_PATH)
                .sameSite(sameSite)
                .maxAge(Duration.ofMillis(refreshTokenMillis))
                .build();
    }

}
