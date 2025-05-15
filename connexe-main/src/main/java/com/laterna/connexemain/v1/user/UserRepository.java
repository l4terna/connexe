package com.laterna.connexemain.v1.user;

import io.netty.util.AsyncMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.email IN :emails")
    Set<Long> findUserIdsByEmails(Set<String> emails);

    boolean existsByLogin(String login);

    Page<User> findByLoginLikeIgnoreCase(String login, Pageable pageable);
}
