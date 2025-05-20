package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1.channel.Channel;
import com.laterna.connexemain.v1.channel.ChannelService;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.HubService;
import com.laterna.connexemain.v1.message.dto.CreateMessageDTO;
import com.laterna.connexemain.v1.message.dto.GetMessagesFilter;
import com.laterna.connexemain.v1.message.dto.MessageDTO;
import com.laterna.connexemain.v1.message.dto.UpdateMessageDTO;
import com.laterna.connexemain.v1.message.event.MessageCreatedEvent;
import com.laterna.connexemain.v1.message.event.MessageDeletedEvent;
import com.laterna.connexemain.v1.message.event.MessageUpdatedEvent;
import com.laterna.connexemain.v1.message.attachment.MessageAttachmentService;
import com.laterna.connexemain.v1.message.read.MessageReadStatus;
import com.laterna.connexemain.v1.message.read.MessageReadStatusService;
import com.laterna.connexemain.v1.message.read.enumeration.MessageStatus;
import com.laterna.connexemain.v1.permission.PermissionService;
import com.laterna.connexemain.v1.permission.enumeration.Permission;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final ChannelService channelService;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final PermissionService permissionService;
    private final HubService hubService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageReadStatusService messageReadStatusService;
    private final MessageAttachmentService messageAttachmentService;

    @Transactional
    public MessageDTO create(Long channelId, CreateMessageDTO createMessageDTO, User currentUser) {
        Channel channel = channelService.findChannelById(channelId);

        if (channel.getType() == ChannelType.VOICE || channel.getType() == ChannelType.TEXT) {
            Hub hub = hubService.findHubByChannelId(channel.getId());
            permissionService.hasPermissionsThrow(currentUser.getId(), hub.getId(), Permission.SEND_MESSAGES);
        }

        Message message = Message.builder()
                .content(createMessageDTO.content())
                .author(currentUser)
                .channelId(channel.getId())
                .build();

        MessageDTO newMessageDTO;

        if (channel.getType() == ChannelType.DC) {
            newMessageDTO = messageMapper.toDTO(messageRepository.save(message), MessageStatus.SENT);
        } else if (channel.getType() == ChannelType.GROUP_DC || channel.getType() == ChannelType.TEXT) {
            newMessageDTO = messageMapper.toDTO(messageRepository.save(message), MessageStatus.SENT, 0);
        } else {
            newMessageDTO = messageMapper.toDTO(messageRepository.save(message));
        }

        if (createMessageDTO.attachments() != null) {
            messageAttachmentService.save(createMessageDTO.attachments(), newMessageDTO.id(), currentUser.getId());
        }

        messageReadStatusService.createReadStatus(currentUser.getId(), newMessageDTO.id());
        eventPublisher.publishEvent(new MessageCreatedEvent(newMessageDTO, channelId));

        return newMessageDTO;
    }

    @Transactional
    public MessageDTO update(Long channelId, Long messageId, UpdateMessageDTO updateMessageDTO, User currentUser) {
        Message message = findMessageById(messageId);

        if (!message.getChannelId().equals(channelId) ||
                !message.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }

        boolean hasChanges = !message.getContent().equals(updateMessageDTO.content());

        if (!hasChanges) {
            return computeUpdateMessageDTOWithStatus(message);
        }

        message.setContent(updateMessageDTO.content());

        Message savedMessage = messageRepository.save(message);

        MessageDTO updatedMessageDTO = computeUpdateMessageDTOWithStatus(savedMessage);

        eventPublisher.publishEvent(new MessageUpdatedEvent(updatedMessageDTO, channelId));

        return updatedMessageDTO;
    }

    private MessageDTO computeUpdateMessageDTOWithStatus(Message message) {
        long count = messageReadStatusService.countReadStatusesByMessageId(message.getId());
        MessageStatus status = count > 0 ? MessageStatus.READ : MessageStatus.SENT;
        return messageMapper.toDTO(message, status, count);
    }

    @Transactional(readOnly = true)
    public Message findMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }

    @Transactional
    public void delete(Long channelId, Long messageId, User currentUser) {
        Message message = findMessageById(messageId);

        if (!message.getChannelId().equals(channelId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (!message.getAuthor().getId().equals(currentUser.getId())) {
            hiddenMessageService.hide(channelId, messageId, currentUser);
            return;
        }

        Channel channel = channelService.findChannelById(channelId);

        if (channel.getType() == ChannelType.VOICE || channel.getType() == ChannelType.TEXT) {
            Hub hub = hubService.findHubByChannelId(message.getChannelId());
            permissionService.hasPermissionsThrow(currentUser.getId(), hub.getId(), Permission.SEND_MESSAGES);
        }

        messageRepository.delete(message);

        eventPublisher.publishEvent(new MessageDeletedEvent(messageId, channelId));
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getChannelMessages(Long channelId, User currentUser, GetMessagesFilter filter) {
        Channel channel = channelService.findChannelById(channelId);

        Pageable pageable = PageRequest.of(0, filter.getSize(), Sort.Direction.DESC, "id");

        List<Message> messages;

        if (filter.getBefore() != null && filter.getBefore() > 0) {
            messages = messageRepository.findAllByChannelIdAndBeforeId(channelId, filter.getBefore(), pageable);
        } else if (filter.getAfter() != null && filter.getAfter() > 0) {
            messages = messageRepository.findAllByChannelIdAndAfterId(channelId, filter.getAfter(), pageable);
        } else if (filter.getAround() != null && filter.getAround() > 0) {
            messages = messageRepository.findAllByChannelIdAndAroundId(channelId, filter.getAround(), pageable);
        } else {
            messages = messageRepository.findAllByChannelId(channelId, pageable);
        }

        return switch (channel.getType()) {
            case DC -> enrichDirectMessages(messages, currentUser);
            case GROUP_DC, TEXT -> enrichGroupDirectAndTextMessagesWithStatus(messages, currentUser);
            default -> {
                log.error("Unsupported channel type for id {}", channel.getId());
                throw new IllegalAccessError("Unsupported channel type");
            }
        };

    }

    private List<MessageDTO> enrichDirectMessages(List<Message> messages, User currentUser) {
        List<Long> messageIds = messages.stream().map(Message::getId).toList();

        Set<Long> readStatuses =
                messageReadStatusService.findReadStatusesByMessageIdsAndWithoutUserId(messageIds, currentUser.getId())
                        .stream()
                        .map(MessageReadStatus::getMessageId)
                        .collect(Collectors.toSet());

        return messages.stream()
                .map(message -> {
                    if (message.getAuthor().getId().equals(currentUser.getId())) {
                        MessageStatus status =
                                readStatuses.contains(message.getId()) ? MessageStatus.READ : MessageStatus.SENT;

                        return messageMapper.toDTO(message, status);
                    }

                    return messageMapper.toDTO(message);
                })
                .toList();
    }

    private List<MessageDTO> enrichGroupDirectAndTextMessagesWithStatus(List<Message> messages, User currentUser) {
        Set<Long> messageIds = messages.stream().map(Message::getId).collect(Collectors.toSet());

        Map<Long, Set<Long>> readStatusCount = messageReadStatusService.countReadStatusesByMessageIds(messageIds);

        return messages.stream()
                .map(message -> {
                    Set<Long> statusCount = readStatusCount.get(message.getId());

                    if (message.getAuthor().getId().equals(currentUser.getId())) {
                        long count = statusCount == null ? 0 : readStatusCount.get(message.getId())
                                .stream()
                                .filter(rscUserId -> !rscUserId.equals(currentUser.getId()))
                                .count();

                        return messageMapper.toDTO(message, MessageStatus.READ, count);
                    }

                    MessageStatus status =
                            statusCount == null ? MessageStatus.NEW :
                                    statusCount
                                    .stream()
                                    .anyMatch(rscUserId -> rscUserId.equals(currentUser.getId())) ? MessageStatus.READ : MessageStatus.NEW;
                    return messageMapper.toDTO(message, status);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Set<Long> findUnreadMessagesInChannelByIdsForUser(Long userId, Set<Long> messageIds, Long channelId) {
        return messageRepository.findUnreadMessagesInChannelByIdsForUser(userId, messageIds, channelId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findUnreadMessageIdsInChannelForUser(Long userId, Long channelId) {
        return messageRepository.findUnreadMessageIdsInChannelForUser(userId, channelId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findAuthorIdsByMessageIds(Set<Long> messageIds) {
        return messageRepository.findAuthorIdsByMessageIds(messageIds);
    }
}
