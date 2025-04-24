package com.laterna.connexeauth.v1.usersession;

import com.laterna.connexeauth.v1.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByIdAndIsActiveIsTrue(Long id);

    @Query("SELECT us FROM UserSession us " +
            "WHERE us.user = :#{#userSession.user} " +
            "AND us.id != :#{#userSession.id} " +
            "AND us.isActive = true")
    Page<UserSession> findAllAnotherActiveSessions(Pageable pageable, UserSession userSession);

    @Query("SELECT us FROM UserSession us " +
            "WHERE us.fingerprint = :fingerprint " +
            "AND us.user.id = :userId " +
            "AND us.isActive = true ")
    Optional<UserSession> findActiveByFingerprintAndUserId(String fingerprint, Long userId);

    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.fingerprint = :fingerprint AND us.user = :user")
    void deactivateSessionsByFingerprintAndUser(String fingerprint, User user);


    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false " +
            "WHERE us.user = :#{#userSession.user} " +
            "AND us.id != :#{#userSession.id}")
    void deactivateAllOtherSessions(UserSession userSession);

    List<UserSession> user(User user);

    @Query("SELECT us.lastActivity FROM UserSession us " +
            "WHERE us.user.id = :userId " +
            "ORDER BY us.lastActivity DESC " +
            "LIMIT 1")
    Optional<Instant> findMaxLastActivityByUserId(Long userId);
}
