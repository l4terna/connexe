package com.laterna.connexeauth.v1.token.shared;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.laterna.connexeauth.v1._shared.model.entity.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Token extends BaseEntity {
    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRevoked = false;

    @Column(nullable = false)
    private Instant expiresAt;


}
