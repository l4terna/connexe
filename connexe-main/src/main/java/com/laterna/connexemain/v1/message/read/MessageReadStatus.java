package com.laterna.connexemain.v1.message.read;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_read_statuses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"})
)
public class MessageReadStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_read_statuses_seq")
    @SequenceGenerator(
            name = "message_read_statuses_seq",
            allocationSize = 30,
            sequenceName = "message_read_statuses_seq"
    )
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long messageId;

    @Builder.Default
    private Instant readAt = Instant.now();
}