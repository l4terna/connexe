package com.laterna.connexeauth.v1.usersession;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.laterna.connexeauth.v1.token.access.AccessTokenService;
import com.laterna.connexeauth.v1.token.refresh.RefreshTokenService;
import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.usersession.dto.CreateUserSessionDTO;
import com.laterna.connexeauth.v1.usersession.dto.UserSessionDTO;
import com.laterna.proto.v1.UserLastActivityDTO;
import com.laterna.proto.v1.UserLastActivityUpdateDTO;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final HttpServletRequest httpServletRequest;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final UserSessionMapper userSessionMapper;

    @Transactional
    public UserSession create(CreateUserSessionDTO createDTO, String fingerprint) {
        UserSession session =  UserSession.builder()
                .user(createDTO.user())
                .lastActivity(Instant.now())
                .deviceInfo(getDeviceInfo())
                .ipAddress(getClientIpAddress())
                .fingerprint(fingerprint)
                .build();

        return userSessionRepository.save(session);
    }

    private String getDeviceInfo() {
        UserAgent userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader("User-Agent"));

        String deviceInfo = userAgent.getBrowser().getName();
        if(userAgent.getBrowserVersion() != null) {
            deviceInfo += " " + userAgent.getBrowserVersion().getVersion();
        }

        deviceInfo += " " + userAgent.getOperatingSystem().getName();
        deviceInfo += " " + userAgent.getOperatingSystem().getDeviceType();

        return deviceInfo;
    }

    private String getClientIpAddress() {
        String[] IP_HEADERS = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : IP_HEADERS) {
            String value = httpServletRequest.getHeader(header);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                // X-Forwarded-For may contain several IPs, we take the first one
                String[] parts = value.split("\\s*,\\s*");
                return parts[0];
            }
        }

        return httpServletRequest.getRemoteAddr();
    }

    @Transactional
    public void deactivateSessionCompletely(UserSession userSession) {
        userSession.setIsActive(false);

        refreshTokenService.revokeActiveTokens(userSession);
        accessTokenService.revokeActiveTokens(userSession);

        userSessionRepository.save(userSession);
    }

    @Transactional
    public void deactivatePreviousSessions(User user, String fingerprint) {
        userSessionRepository.deactivateSessionsByFingerprintAndUser(fingerprint, user);
        accessTokenService.revokeActiveTokensByFingerprintAndUser(fingerprint, user);
        refreshTokenService.revokeActiveTokensByFingerprintAndUser(fingerprint, user);
    }

    @Transactional(readOnly = true)
    public Page<UserSessionDTO> getUserSessions(Pageable pageable, User currentUser, String fingerprint) {
        return userSessionRepository.findAllAnotherActiveSessions(
                pageable, findUserSessionByUserIdAndFingerprint(currentUser.getId(), fingerprint)
                )
                .map(userSessionMapper::toDTO);
    }

    @Transactional
    public void deactivateSession(Long id, User user) {
        UserSession userSession = userSessionRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!userSession.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Session is not active");
        }

        userSession.setIsActive(false);

        accessTokenService.revokeActiveTokens(userSession);
        refreshTokenService.revokeActiveTokens(userSession);

        userSessionRepository.save(userSession);
    }

    @Transactional
    public void deactivateAllOtherUserSessions(User currentUser, String fingerprint) {
        UserSession userSession = findUserSessionByUserIdAndFingerprint(currentUser.getId(), fingerprint);
        userSessionRepository.deactivateAllOtherSessions(userSession);
    }

    @Transactional(readOnly = true)
    public UserSession findUserSessionByUserIdAndFingerprint(Long userId, String fingerprint) {
        return userSessionRepository.findActiveByFingerprintAndUserId(fingerprint, userId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

    @Transactional
    public void updateLastActivity(Long userId, UserLastActivityUpdateDTO lastActivityUpdate) {
        UserSession session = userSessionRepository.findActiveByFingerprintAndUserId(lastActivityUpdate.getFingerprint(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        session.setLastActivity(Instant.ofEpochMilli(lastActivityUpdate.getLastActivityTimestamp()));
        userSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public String getLastActivity(Long userId) {
        Instant lastActivity = userSessionRepository.findMaxLastActivityByUserId(userId).orElse(Instant.now());

        try {
            return JsonFormat.printer().preservingProtoFieldNames().print(UserLastActivityDTO.newBuilder().setLastActivityTimestamp(lastActivity.toEpochMilli()).build());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
