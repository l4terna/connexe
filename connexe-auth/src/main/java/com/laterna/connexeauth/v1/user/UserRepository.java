package com.laterna.connexeauth.v1.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);

    @Query("SELECT u.id FROM User u WHERE u.email IN :emails")
    Set<Long> findUserIdsByEmails(Set<String> emails);
}
