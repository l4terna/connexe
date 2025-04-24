package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1._shared.websocket.dto.WebSocketMessage;
import com.laterna.connexemain.v1.message.dto.MessageDTO;
import com.laterna.connexemain.v1.message.read.MessageReadStatusService;
import com.laterna.connexemain.v1.message.read.enumeration.MessageStatus;
import com.laterna.connexemain.v1.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MessageWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final UserService userService;
    private final MessageReadStatusService messageReadStatusService;

    private Set<String> getActiveWebSocketUsers(Long channelId) {
        return simpUserRegistry.getUsers().stream()
                .filter(user -> user.getSessions()
                        .stream()
                        .anyMatch(session -> session.getSubscriptions()
                                .stream()
                                .anyMatch(sub ->
                                        sub.getDestination().endsWith("/channels/" + channelId +"/messages")))
                )
                .map(SimpUser::getName)
                .collect(Collectors.toSet());
    }

    public void messageCreated(MessageDTO message) {
        Set<Long> userIds = userService.findUserIdsByEmails(getActiveWebSocketUsers(message.channelId()));
        Set<Long> userReadStatus = messageReadStatusService.findReadStatusesByMessageIdAndUserIds(message.id(), userIds);

        userIds.forEach(userId -> {
            MessageDTO messageWithStatus = computeMessageDTOWithStatus(message, userId, userReadStatus);

            WebSocketMessage wsMessage = WebSocketMessage.builder("MESSAGE_CREATE")
                    .add("message", messageWithStatus)
                    .build();

            send(userId.toString(), message.channelId(), wsMessage);
        });

    }

    public void messageUpdated(MessageDTO message) {
        Set<Long> userIds = userService.findUserIdsByEmails(getActiveWebSocketUsers(message.channelId()));

        Set<Long> userReadStatus = messageReadStatusService.findReadStatusesByMessageIdAndUserIds(message.id(), userIds);

        userIds.forEach(userId -> {
            MessageDTO messageWithStatus = computeMessageDTOWithStatus(message, userId, userReadStatus);

            WebSocketMessage wsMessage = WebSocketMessage.builder("MESSAGE_UPDATE")
                    .add("message", messageWithStatus)
                    .build();

            send(userId.toString(), message.channelId(), wsMessage);
        });
    }

    private MessageDTO computeMessageDTOWithStatus(MessageDTO message, Long userId, Set<Long> userReadStatus) {
        if (userId.equals(message.author().id())) {
            Integer status = userReadStatus.isEmpty() ? MessageStatus.SENT.getValue() : MessageStatus.READ.getValue();

            return message.toBuilder()
                    .status(status)
                    .readByCount((long) userReadStatus.size())
                    .build();
        } else {
            Integer status = userReadStatus.contains(userId) ? MessageStatus.READ.getValue() : MessageStatus.NEW.getValue();

            return message.toBuilder()
                    .status(status)
                    .readByCount(null)
                    .build();
        }
    }

    public void messageDeleted(Long messageId, Long channelId) {
        WebSocketMessage wsMessage = WebSocketMessage.builder("MESSAGE_DELETE")
                .add("messageId", messageId)
                .add("channelId", channelId)
                .build();

        send(channelId, wsMessage);
    }

    private void send(Long channelId, WebSocketMessage message) {
        messagingTemplate.convertAndSend(
                "/v1/topic/channels/" + channelId + "/messages",
                message
        );
    }

    private void send(String userId, Long channelId, WebSocketMessage message) {
        messagingTemplate.convertAndSendToUser(
                userId,
                    "/queue/channels/" + channelId + "/messages",
                message
        );
    }
}
