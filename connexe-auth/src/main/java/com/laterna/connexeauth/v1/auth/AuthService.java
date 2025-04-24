package com.laterna.connexeauth.v1.auth;

import com.laterna.connexeauth.v1.auth.dto.AuthDTO;
import com.laterna.connexeauth.v1.auth.dto.LoginDTO;
import com.laterna.connexeauth.v1.auth.dto.RegisterDTO;
import com.laterna.connexeauth.v1.jwt.JwtService;
import com.laterna.connexeauth.v1.token.access.AccessTokenService;
import com.laterna.connexeauth.v1.token.refresh.RefreshToken;
import com.laterna.connexeauth.v1.token.refresh.RefreshTokenService;
import com.laterna.connexeauth.v1.token.shared.Token;
import com.laterna.connexeauth.v1.token.shared.dto.CreateTokenDTO;
import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.user.UserDetailsServiceImpl;
import com.laterna.connexeauth.v1.user.UserMapper;
import com.laterna.connexeauth.v1.user.UserService;
import com.laterna.connexeauth.v1.usersession.UserSession;
import com.laterna.connexeauth.v1.usersession.UserSessionService;
import com.laterna.connexeauth.v1.usersession.dto.CreateUserSessionDTO;
import com.laterna.proto.v1.UserDataProto;
import com.laterna.proto.v1.UserEvent;
import com.laterna.proto.v1.UserEventProduce;
import com.laterna.proto.v1.UserEventType;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccessTokenService accessTokenService;
    private final UserSessionService userSessionService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public AuthDTO register(RegisterDTO registerDTO, HttpServletResponse response) {
        saveFingerprintCookie(response, registerDTO.fingerprint());
        User user = userService.createUser(registerDTO);
        Token accessToken = createAuthenticationSession(user, registerDTO.fingerprint(), response);

        UserEvent event = UserEvent.newBuilder()
                .setEventType(UserEventType.USER_CREATED)
                .setCreatedUser(UserDataProto.newBuilder()
                        .setUserId(user.getId())
                        .setLogin(user.getLogin())
                        .setEmail(user.getEmail())
                        .setCreatedAt(user.getCreatedAt().toEpochMilli())
                        .setUpdatedAt(user.getLastModifiedAt() != null ?
                                user.getLastModifiedAt().toEpochMilli() : 0)
                        .build())
                .build();

        applicationEventPublisher.publishEvent(UserEventProduce.newBuilder().setUserEvent(event).build());

        return AuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(accessToken.getToken())
                .build();
    }

    @Transactional
    public AuthDTO login(LoginDTO loginDTO, HttpServletResponse response) {
        if (!userService.matchPassword(loginDTO.email(), loginDTO.password())) {
            throw new AccessDeniedException("Invalid email or password");
        }

        saveFingerprintCookie(response, loginDTO.fingerprint());
        User user = userService.findUserByEmail(loginDTO.email());
        userSessionService.deactivatePreviousSessions(user, loginDTO.fingerprint());
        Token accessToken = createAuthenticationSession(user, loginDTO.fingerprint(), response);

        return AuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(accessToken.getToken())
                .build();
    }

    private void saveFingerprintCookie(HttpServletResponse response, String fingerprint) {
        ResponseCookie fingerprintCookie = ResponseCookie.from("__fprid", fingerprint)
                .httpOnly(true)
                .secure(false)
//                .partitioned(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtService.getJwtRefreshExpiration())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, fingerprintCookie.toString());
    }

    private Token createAuthenticationSession(User user, String fingerprint, HttpServletResponse response) {
        CreateUserSessionDTO createUserSessionDTO = CreateUserSessionDTO.builder()
                .user(user)
                .build();
        UserSession userSession = userSessionService.create(createUserSessionDTO, fingerprint);

        CreateTokenDTO createTokenDTO = CreateTokenDTO.builder()
                .userSession(userSession)
                .user(user)
                .build();

        accessTokenService.revokeActiveTokens(userSession);

        Token accessToken = accessTokenService.createToken(createTokenDTO);
        Token refreshToken = refreshTokenService.createToken(createTokenDTO);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("__rtid", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
//                .partitioned(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .maxAge(jwtService.getJwtRefreshExpiration())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return accessToken;
    }

    @Transactional
    public void logout(String refreshToken, String fingerprint, HttpServletResponse response) {
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken);

        if (!refreshTokenService.validateToken(refreshToken, fingerprint)) {
            userSessionService.deactivateSessionCompletely(refreshTokenEntity.getUserSession());

            throw new AccessDeniedException("Invalid refresh token");
        }

        userSessionService.deactivateSessionCompletely(refreshTokenEntity.getUserSession());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("__rtid", "")
                .httpOnly(true)
                .maxAge(0)
                .secure(false)
//                .partitioned(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .build();

        ResponseCookie fingerprintCookie = ResponseCookie.from("__fprid", fingerprint)
                .httpOnly(true)
                .maxAge(0)
                .secure(false)
//                .partitioned(true)
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, fingerprintCookie.toString());
    }

    @Transactional
    public AuthDTO refreshToken(String refreshToken, String fingerprint) {
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken);

        if (!refreshTokenService.validateToken(refreshToken, fingerprint)) {
            userSessionService.deactivateSessionCompletely(refreshTokenEntity.getUserSession());

            throw new AccessDeniedException("Invalid refresh token");
        }

        accessTokenService.revokeActiveTokens(refreshTokenEntity.getUserSession());

        CreateTokenDTO createTokenDTO = CreateTokenDTO.builder()
                .userSession(refreshTokenEntity.getUserSession())
                .user(refreshTokenEntity.getUserSession().getUser())
                .build();

        Token accessToken = accessTokenService.createToken(createTokenDTO);

        return AuthDTO.builder()
                .user(userMapper.toDTO(refreshTokenEntity.getUserSession().getUser()))
                .token(accessToken.getToken())
                .build();
    }

    @Transactional
    public Long validateToken(String authHeader) {
//            String fingerprint = userSessionService.getFingerprint(); // TODO: ИСПРАВИТЬ НА ПРОДЕ
        String fingerprint = "string";

        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String jwtToken = authHeader.substring(7);

        String username = jwtService.extractUsername(jwtToken);

        if (StringUtils.isEmpty(username) || !accessTokenService.validateToken(jwtToken, fingerprint)) {
            return null;
        }

        User user = userDetailsServiceImpl.loadUserByUsername(username);

        if (!jwtService.isTokenValid(jwtToken, user)) {
            return null;
        }

        return user.getId();
    }
}
