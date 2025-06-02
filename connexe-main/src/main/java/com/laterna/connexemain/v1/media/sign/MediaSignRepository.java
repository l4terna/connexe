package com.laterna.connexemain.v1.media.sign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface MediaSignRepository extends JpaRepository<MediaSign, Long> {
    Optional<MediaSign> findBySignAndUserId(String sign, Long userId);
}
