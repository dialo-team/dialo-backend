package com.fit.se.infrastructure.persistence.entity.label;

import com.fit.se.domain.label.aggregate.LabelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "dialo_conversation_label")
@Table(name = "dialo_conversation_label")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LabelType type;

    @Column(nullable = false)
    private boolean deletable;
}