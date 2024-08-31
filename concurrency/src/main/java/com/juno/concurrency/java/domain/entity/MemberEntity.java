package com.juno.concurrency.java.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private int number;

}
