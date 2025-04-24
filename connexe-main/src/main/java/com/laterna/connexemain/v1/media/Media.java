package com.laterna.connexemain.v1.media;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import com.laterna.connexemain.v1.media.enumeration.MediaVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media")
@EntityListeners(AuditingEntityListener.class)
public class Media extends IdEntity {
    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaVisibility visibility;

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

    private Long createdBy;
}
