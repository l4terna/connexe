package com.laterna.connexemain.v1.hub.invite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByCode(String code);
    Page<Invite> findAllByHubId(Long hubId, Pageable pageable);
}
