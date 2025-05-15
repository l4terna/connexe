package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.message.dto.MessageDTO;
import com.laterna.connexemain.v1.message.read.enumeration.MessageStatus;
import com.laterna.connexemain.v1.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    @Mapping(target = "readByCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    MessageDTO toDTO(Message message);

    @Mapping(target = "status", expression = "java(status.getValue())")
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    @Mapping(target = "readByCount", ignore = true)
    MessageDTO toDTO(Message message, MessageStatus status);

    @Mapping(target = "readByCount", expression = "java(readByCount)")
    @Mapping(target = "status", expression = "java(status.getValue())")
    @Mapping(target = "attachments", expression = "java(getAttachmentKeys(message))")
    MessageDTO toDTO(Message message, MessageStatus status, long readByCount);

    default List<String> getAttachmentKeys(Message message) {
        return message.getAttachments() != null ? message.getAttachments()
                .stream()
                .map(Media::getStorageKey)
                .toList() : null;
    }
}
