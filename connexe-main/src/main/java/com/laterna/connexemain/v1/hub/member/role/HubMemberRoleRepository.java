package com.laterna.connexemain.v1.hub.member.role;

import com.laterna.connexemain.v1.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface HubMemberRoleRepository extends JpaRepository<HubMemberRole, Long> {
    boolean existsByHubMemberIdAndRoleId(Long memberId, Long roleId);
    Optional<HubMemberRole> findByHubMemberIdAndRoleId(Long memberId, Long roleId);
    void deleteAllByRoleId(Long roleId);

    Long role(Role role);
}
