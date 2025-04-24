package com.laterna.connexeauth.v1.token.shared;

import com.laterna.connexeauth.v1.token.shared.dto.CreateTokenDTO;
import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.usersession.UserSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public abstract class TokenService<T extends Token> {
    protected final TokenRepository<T> tokenRepository;

    protected TokenService(TokenRepository<T> tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Token createToken(CreateTokenDTO createDTO) {
        String jwtToken = generateToken(createDTO.user());

        Instant expiresAt = Instant.now().plusMillis(getExpirationTime() * 1000);

        T token = createTokenEntity(
                CreateTokenDTO.builder()
                        .userSession(createDTO.userSession())
                        .user(createDTO.user())
                        .token(jwtToken)
                        .build(),
                expiresAt
        );

        return tokenRepository.save(token);
    }

    @Transactional
    public void revokeActiveTokens(UserSession userSession) {
        tokenRepository.revokeAllActiveTokensByUserSession(userSession);
    }

    @Transactional
    public void revokeActiveTokensByFingerprintAndUser(String fingerprint, User user) {
        tokenRepository.revokeActiveTokensByFingerprintAndUser(fingerprint, user);
    }

    public abstract boolean validateToken(String token, String fingerprint);
    protected abstract long getExpirationTime();
    protected abstract String generateToken(User user);
    protected abstract T createTokenEntity(CreateTokenDTO dto, Instant expiresAt);
}
