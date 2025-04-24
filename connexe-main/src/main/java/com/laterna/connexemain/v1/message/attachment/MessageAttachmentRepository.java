package com.laterna.connexemain.v1.message.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Long> {
}
