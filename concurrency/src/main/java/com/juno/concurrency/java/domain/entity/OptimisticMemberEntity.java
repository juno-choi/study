package com.juno.concurrency.java.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "O_MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class OptimisticMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private int number;

    @Version
    private int version;
}
