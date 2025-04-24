package com.laterna.connexemain.v1.channel.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    @Query("""
    SELECT cm.user.id FROM ChannelMember cm JOIN cm.channel c
    WHERE c.id = :channelId
    AND c.type = 'GROUP_DC'
""")
    Set<Long> findChannelUserIds(Long channelId);

    @Query("""
    SELECT COUNT(*) > 0 FROM Channel c JOIN ChannelMember cm ON cm.channel.id = c.id
    WHERE cm.user.id = :userId AND c.id = :channelId
""")
    boolean existsByIdAndUserId(Long channelId, Long userId);

    @Query("""
    SELECT cm FROM ChannelMember cm
    WHERE cm.channel.id = :channelId
    ORDER BY
        CASE WHEN cm.user.id IN :userIds THEN cm.user.id ELSE cm.user.id END,
        cm.user.id ASC
""")
    Page<ChannelMember> findAllByChannelIdAndSortByUserIds(Long channelId, Iterable<Long> userIds, Pageable pageable);
}
