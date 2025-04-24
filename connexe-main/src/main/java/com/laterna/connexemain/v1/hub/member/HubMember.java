package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import com.laterna.connexemain.v1.hub.Hub;
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
@Table(name = "hub_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "user_id"})
)
public class HubMember extends IdEntity {
    @ManyToOne
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column(name = "joined_at")
    private Instant joinedAt = Instant.now();
}
