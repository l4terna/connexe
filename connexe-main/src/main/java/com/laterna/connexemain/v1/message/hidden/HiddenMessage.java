package com.laterna.connexemain.v1.message.hidden;

import com.laterna.connexemain.v1._shared.model.entity.BaseEntity;
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
@Table(name = "hidden_messages")
public class HiddenMessage extends BaseEntity {
    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private Long userId;
}
