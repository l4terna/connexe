package com.laterna.connexemain.v1.hub.member.role;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hub_member_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_member_id", "role_id"})
)
public class HubMemberRole extends IdEntity {
    @Column(nullable = false)
    private Long hubMemberId;

    @Column(nullable = false)
    private Long roleId;
}
