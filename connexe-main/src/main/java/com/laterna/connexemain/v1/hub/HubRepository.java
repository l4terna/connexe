package com.laterna.connexemain.v1.hub;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
interface HubRepository extends JpaRepository<Hub, Long> {
    @Query("""
    SELECT h FROM Hub h
    JOIN Category ct ON ct.hub.id = h.id
    JOIN Channel ch ON ch.categoryId = ct.id
    WHERE ch.id = :channelId
""")
    Optional<Hub> findHubByChannelId(Long channelId);

    @Query("""
    SELECT h FROM Hub h
    JOIN HubMember hm ON h.id = hm.hub.id
    WHERE hm.user.id = :userId
""")
    Page<Hub> findAllByUserId(Pageable pageable, Long userId);

    @Query("""
    SELECT h.id FROM Hub h
    JOIN HubMember hm ON h.id = hm.hub.id
    LEFT JOIN HubMemberRole hmr ON hmr.hubMemberId = hm.id
    LEFT JOIN Role r ON hmr.roleId = r.id
    WHERE h.owner.id = :userId OR hm.user.id = :userId
    GROUP BY h.id
""")
    Set<Long> findAllHubIdsByUserId(Long userId);
}
