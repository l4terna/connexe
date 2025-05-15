package com.laterna.connexemain.v1.message.read;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageReadStatusService {
    private final MessageReadStatusRepository messageReadStatusRepository;

    @Transactional
    public MessageReadStatus createReadStatus(Long userId, Long messageId) {
        MessageReadStatus mrs = MessageReadStatus.builder()
                .userId(userId)
                .messageId(messageId)
                .build();

        return messageReadStatusRepository.save(mrs);
    }

    @Transactional(readOnly = true)
    public Set<MessageReadStatus> findReadStatusesByMessageIdsAndWithoutUserId(List<Long> messageIds, Long userId) {
        return messageReadStatusRepository.findAllByMessageIdsAndWithoutUserId(messageIds, userId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findReadStatusesByMessageIdAndUserIds(Long messageId, Set<Long> userIds) {
        return messageReadStatusRepository.findAllByMessageIdAndUserIds(messageId, userIds)
                .stream()
                .map(MessageReadStatus::getUserId)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Map<Long, Set<Long>> countReadStatusesByMessageIds(Set<Long> messageIds) {
        return messageReadStatusRepository.countByMessageIds(messageIds)
                .stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0], // messageId
                        Collectors.mapping(
                                row -> (Long) row[1], // userId
                                Collectors.toSet() // Set<Long>
                        )
                ));
    }

    @Transactional(readOnly = true)
    public long countReadStatusesByMessageId(Long messageId) {
        return messageReadStatusRepository.countByMessageId(messageId);
    }
}
