package com.laterna.connexemain.v1.message.attachment;

import com.laterna.connexemain.v1.media.LocalMediaStorageService;
import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.media.enumeration.MediaVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageAttachmentService {
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final LocalMediaStorageService localMediaStorageService;

    @Transactional
    public void save(MultipartFile[] files, Long messageId, Long userId) {
        List<MessageAttachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            Media media = localMediaStorageService.store(file, MediaVisibility.PUBLIC, userId);

            MessageAttachment attachment = MessageAttachment.builder()
                    .mediaId(media.getId())
                    .messageId(messageId)
                    .build();

            attachments.add(attachment);
        }

        messageAttachmentRepository.saveAll(attachments);
    }
}
