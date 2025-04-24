package com.laterna.connexemain.v1.channel.member;


import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import com.laterna.connexemain.v1.channel.Channel;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel_members",
    uniqueConstraints = @UniqueConstraint(columnNames = {"channel_id", "user_id"})
)
public class ChannelMember extends IdEntity {
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(name = "joined_at")
    private Instant joinedAt = Instant.now();
}
