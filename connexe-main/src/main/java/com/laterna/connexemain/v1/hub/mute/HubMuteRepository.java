package com.laterna.connexemain.v1.hub.mute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface HubMuteRepository extends JpaRepository<HubMute, Long> {
    Optional<HubMute> findByHubIdAndUserId(Long hubId, Long userId);
}
