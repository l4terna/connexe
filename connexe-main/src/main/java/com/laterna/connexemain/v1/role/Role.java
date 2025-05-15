package com.laterna.connexemain.v1.role;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.hub.member.role.HubMemberRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnTransformer;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String permissions;

    @Column(nullable = false)
    @ColumnTransformer(write = "?::bit varying")
    private String permissionsMask;

    @Column(nullable = false)
    @Builder.Default
    private Long priority = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id", nullable = false)
    private Hub hub;

    @OneToMany(mappedBy = "role", orphanRemoval = true)
    private Set<HubMemberRole> hubMemberRoles = new HashSet<>();
}
