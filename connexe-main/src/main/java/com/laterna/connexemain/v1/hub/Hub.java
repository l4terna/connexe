package com.laterna.connexemain.v1.hub;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.hub.enumeration.HubType;
import com.laterna.connexemain.v1.media.Media;
import com.laterna.connexemain.v1.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hubs")
public class Hub extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HubType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hub_avatars",
            joinColumns = @JoinColumn(name = "hub_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id", referencedColumnName = "id")
    )
    private Media avatar;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
