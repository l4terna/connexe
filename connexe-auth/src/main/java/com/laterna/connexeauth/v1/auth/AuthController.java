package com.laterna.connexeauth.v1.auth;

import com.laterna.connexeauth.v1.auth.dto.AuthDTO;
import com.laterna.connexeauth.v1.auth.dto.LoginDTO;
import com.laterna.connexeauth.v1.auth.dto.RegisterDTO;
import com.laterna.connexeauth.v1.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> register(
            @Valid @RequestBody RegisterDTO registerDTO,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.register(registerDTO, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(loginDTO, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO> refresh(
            @CookieValue(name = "__rtid") String refreshToken,
            @CookieValue("__fprid") String fingerprint
    ) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken, fingerprint));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "__rtid") String refreshToken,
            @CookieValue("__fprid") String fingerprint,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, fingerprint, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Long> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @CookieValue("__fprid") String fingerprint
    ) {
        Long validationResult = authService.validateToken(authHeader, fingerprint);

        return validationResult == null ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(validationResult);
    }
}
