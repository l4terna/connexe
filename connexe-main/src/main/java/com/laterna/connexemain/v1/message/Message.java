package com.laterna.connexemain.v1.message;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.message.attachment.MessageAttachment;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Long channelId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Message reply;

    @OneToMany(mappedBy = "message")
    private List<MessageAttachment> attachments;
}
