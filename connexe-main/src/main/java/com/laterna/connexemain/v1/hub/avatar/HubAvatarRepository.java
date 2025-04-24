package com.laterna.connexemain.v1.hub.avatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface HubAvatarRepository extends JpaRepository<HubAvatar, Long> {
    void deleteByHubId(Long hubId);
}
