package com.laterna.connexeauth.v1.usersession;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.laterna.connexeauth.v1._shared.model.entity.BaseEntity;
import com.laterna.connexeauth.v1.user.User;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_sessions")
public class UserSession extends BaseEntity {
    @Column(nullable = false)
    private Instant lastActivity;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String deviceInfo;

    @Column(nullable = false)
    private String fingerprint;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}