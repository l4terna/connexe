package com.laterna.connexemain.v1.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"reply", "attachments.media"})
    @Query("SELECT m FROM Message m " +
            "WHERE m.channelId = :channelId " +
            "AND m.id NOT IN (SELECT hm.messageId FROM HiddenMessage hm WHERE hm.userId = :userId) " +
            "AND m.content ILIKE :search")
    List<Message> findAllByChannelId(Long channelId, Long userId, String search, Pageable pageable);

    @EntityGraph(attributePaths = {"reply", "attachments.media"})
    @Query("SELECT m FROM Message m " +
            "WHERE m.channelId = :channelId AND m.id < :beforeId " +
            "AND m.id NOT IN (SELECT hm.messageId FROM HiddenMessage hm WHERE hm.userId = :userId) " +
            "AND m.content ILIKE :search " +
            "ORDER BY m.id DESC")
    List<Message> findAllByChannelIdAndBeforeId(Long channelId, Long beforeId, Long userId, String search, Pageable pageable);

    @EntityGraph(attributePaths = {"reply", "attachments.media"})
    @Query("SELECT m FROM Message m " +
            "WHERE m.channelId = :channelId AND m.id > :afterId " +
            "AND m.id NOT IN (SELECT hm.messageId FROM HiddenMessage hm WHERE hm.userId = :userId) " +
            "ORDER BY m.id ASC")
    List<Message> findAllByChannelIdAndAfterId(Long channelId, Long afterId, Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"reply", "attachments.media"})
    @Query("SELECT ma FROM Message ma " + // messages above
            "WHERE ma.id < :aroundId AND ma.id NOT IN (SELECT hm.messageId FROM HiddenMessage hm WHERE hm.userId = :userId) " +
            "ORDER BY ma.id desc " +
            "LIMIT :size " +
            "UNION ALL " +
            "SELECT mb FROM Message mb " + // messages below
            "WHERE mb.id >= :aroundId AND mb.id NOT IN (SELECT hm.messageId FROM HiddenMessage hm WHERE hm.userId = :userId) " +
            "ORDER BY mb.id asc " +
            "LIMIT :size ")
    List<Message> findAllByChannelIdAndAroundId(Long channelId, Long aroundId, Long userId, Integer size);

    @Query("SELECT DISTINCT m.id FROM Message m " +
            "LEFT JOIN MessageReadStatus mrs ON mrs.messageId = m.id AND mrs.userId = :userId " +
            "WHERE m.id IN :messageIds " +
            "AND m.channelId = :channelId " +
            "AND mrs IS NULL")
    Set<Long> findUnreadMessagesInChannelByIdsForUser(Long userId, Set<Long> messageIds, Long channelId);

    @Query("SELECT DISTINCT m.id FROM Message m " +
            "LEFT JOIN MessageReadStatus mrs ON mrs.messageId = m.id AND mrs.userId = :userId " +
            "WHERE m.channelId = :channelId AND mrs IS NULL")
    Set<Long> findUnreadMessageIdsInChannelForUser(Long userId, Long channelId);

    @Query("SELECT DISTINCT m.author.id FROM Message m " +
            "WHERE m.id IN :messageIds")
    Set<Long>   findAuthorIdsByMessageIds(Set<Long> messageIds);
}
