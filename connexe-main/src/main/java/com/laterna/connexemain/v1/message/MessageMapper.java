package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.message.attachment.MessageAttachment;
import com.laterna.connexemain.v1.message.dto.MessageDTO;
import com.laterna.connexemain.v1.message.dto.SimpleMessageDTO;
import com.laterna.connexemain.v1.message.read.enumeration.MessageStatus;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    @Mapping(target = "readByCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reply", source = "message.reply", qualifiedByName = "toSimpleDTO")
    MessageDTO toDTO(Message message);

    @Mapping(target = "status", expression = "java(status.getValue())")
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    @Mapping(target = "readByCount", ignore = true)
    @Mapping(target = "reply", source = "message.reply", qualifiedByName = "toSimpleDTO")
    MessageDTO toDTO(Message message, MessageStatus status);

    @Mapping(target = "readByCount", expression = "java(readByCount)")
    @Mapping(target = "status", expression = "java(status.getValue())")
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    @Mapping(target = "reply", source = "message.reply", qualifiedByName = "toSimpleDTO")
    MessageDTO toDTO(Message message, MessageStatus status, long readByCount);

    @Mapping(target = "attachmentsCount" , expression = "java(getAttachmentsCount(reply))")
    @Named("toSimpleDTO")
    SimpleMessageDTO toSimpleDTO(Message reply);

    default List<String> getAttachmentKeys(Message message) {
        return message.getAttachments() != null ? message.getAttachments()
                .stream()
                .map(MessageAttachment::getMedia)
                .map(Media::getStorageKey)
                .toList() : null;
    }

    default Integer getAttachmentsCount(Message reply) {
        return reply == null ||
                reply.getAttachments() == null ||
                reply.getAttachments().isEmpty() ? null : reply.getAttachments().size();
    }
}
