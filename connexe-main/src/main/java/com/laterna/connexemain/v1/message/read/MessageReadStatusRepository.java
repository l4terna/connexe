package com.laterna.connexemain.v1.message.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    @Query("SELECT mrs FROM MessageReadStatus mrs " +
            "WHERE mrs.messageId IN :messageIds " +
            "AND mrs.userId != :userId")
    Set<MessageReadStatus> findAllByMessageIdsAndWithoutUserId(List<Long> messageIds, Long userId);

    @Query("SELECT mrs FROM MessageReadStatus mrs " +
            "WHERE mrs.messageId = :messageId AND mrs.userId IN :userIds")
    Set<MessageReadStatus> findAllByMessageIdAndUserIds(Long messageId, Set<Long> userIds);

    @Query("SELECT mrs.messageId, count(*) FROM MessageReadStatus mrs " +
            "WHERE mrs.messageId IN :messageIds " +
            "GROUP BY mrs.messageId")
    List<Long[]> countByMessageIds(Set<Long> messageIds);

    long countByMessageId(Long messageId);

    @Query("SELECT mrs FROM MessageReadStatus mrs " +
            "WHERE mrs.messageId IN :messageIds " +
            "AND mrs.userId = :userId")
    Set<MessageReadStatus> findAllByMessageIdsAndUserId(Set<Long> messageIds, Long userId);
}
