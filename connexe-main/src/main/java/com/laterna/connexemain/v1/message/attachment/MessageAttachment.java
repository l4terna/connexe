package com.laterna.connexemain.v1.message.attachment;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.message.Message;
import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(nullable = false, name = "message_id")
    private Message message;

    @OneToOne
    @JoinColumn(name = "media_id")
    private Media media;
}
