package com.laterna.connexemain.v1.message.attachment;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_attachments")
public class MessageAttachment extends IdEntity {
    @Column(nullable = false, name = "message_id")
    private Long messageId;

    @Column(nullable = false, name = "media_id")
    private Long mediaId;
}
