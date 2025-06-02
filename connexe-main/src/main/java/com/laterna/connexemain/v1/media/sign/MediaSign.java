package com.laterna.connexemain.v1.media.sign;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.media.Media;
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
@Table(name = "media_sign")
public class MediaSign extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "sign")
    private String sign;
}
