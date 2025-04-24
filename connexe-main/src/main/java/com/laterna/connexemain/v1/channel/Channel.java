package com.laterna.connexemain.v1.channel;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
import com.laterna.connexemain.v1.channel.enumeration.ChannelType;
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
@Table(name = "channels")
public class Channel extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "category_id")
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    private Integer position;
    private String name;
}
