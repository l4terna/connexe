package com.laterna.connexemain.v1.hub.mute;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hub_mutes")
public class HubMute extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long hubId;

    @Column(nullable = false)
    private Instant expiresAt;
}
