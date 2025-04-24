package com.laterna.connexeauth.v1.token.shared;

import com.laterna.connexeauth.v1.user.User;
import com.laterna.connexeauth.v1.usersession.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {
    Optional<T> findByToken(String token);

    @Modifying
    @Query("UPDATE #{#entityName} t SET t.isRevoked = true WHERE t.userSession = :userSession AND t.isRevoked = false")
    void revokeAllActiveTokensByUserSession(UserSession userSession);

    @Modifying
    @Query("UPDATE #{#entityName} t SET t.isRevoked = true " +
            "WHERE t.userSession.fingerprint = :fingerprint " +
            "AND t.userSession.user = :user")
    void revokeActiveTokensByFingerprintAndUser(String fingerprint, User user);
}
