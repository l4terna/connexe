package com.laterna.connexemain.v1.user.settings.p2p;


import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p2p_settings")
public class P2PSettings extends BaseEntity {
    @Column(nullable = false)
    private Long targetUserId;

    @Column(nullable = false)
    private Long sourceUserId;

    @Column(nullable = false)
    private Boolean isMuted;

    @Column(nullable = false)
    private Integer volumeLevel;
}
