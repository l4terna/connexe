package com.laterna.connexemain.v1.hub.invite;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invites")
public class Invite extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, columnDefinition = "maxUses = null = endless uses")
    private Integer maxUses;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentUses = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}
