package com.laterna.connexemain.v1.hub.avatar;

import com.laterna.connexemain.v1._shared.model.entity.IdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "hub_avatars")
public class HubAvatar extends IdEntity {
    @Column(name = "hub_id", nullable = false)
    private Long hubId;

    @Column(name = "media_id", nullable = false)
    private Long mediaId;
}
