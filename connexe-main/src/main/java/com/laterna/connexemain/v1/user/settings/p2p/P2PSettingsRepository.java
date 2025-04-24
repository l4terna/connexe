package com.laterna.connexemain.v1.user.settings.p2p;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface P2PSettingsRepository extends JpaRepository<P2PSettings, Long> {
    Optional<P2PSettings> findByTargetUserIdAndSourceUserId(Long targetUserId, Long sourceUserId);

    List<P2PSettings> findAllBySourceUserIdAndTargetUserIdIn(Long sourceUserId, Iterable<Long> targetUserIds);
}
