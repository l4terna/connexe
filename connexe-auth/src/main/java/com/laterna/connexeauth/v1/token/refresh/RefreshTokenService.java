package com.laterna.connexeauth.v1.token.refresh;

import com.laterna.connexeauth.v1.jwt.JwtService;
import com.laterna.connexeauth.v1.token.shared.TokenService;
import com.laterna.connexeauth.v1.token.shared.dto.CreateTokenDTO;
import jakarta.persistence.EntityNotFoundException;
import com.laterna.connexeauth.v1.user.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService extends TokenService<RefreshToken> {
    private final JwtService jwtService;

    public RefreshTokenService(
            RefreshTokenRepository tokenRepository,
            JwtService jwtService) {
        super(tokenRepository);
        this.jwtService = jwtService;
    }

    @Override
    protected long getExpirationTime() {
        return jwtService.getJwtRefreshExpiration();
    }

    @Override
    protected String generateToken(User user) {
        return jwtService.generateRefreshToken(user);
    }

    @Override
    protected RefreshToken createTokenEntity(CreateTokenDTO dto, Instant expiresAt) {
        return RefreshToken.builder()
                .userSession(dto.userSession())
                .token(dto.token())
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public boolean validateToken(String refreshToken, String fingerprint) {
        RefreshToken token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));

        return token.getUserSession().getFingerprint().equals(fingerprint)
                && token.getExpiresAt().isAfter(Instant.now())
                && !token.getIsRevoked();
    }

    public RefreshToken findByToken(String refreshToken) {
        return tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
    }
}
