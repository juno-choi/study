package com.juno.concurrency.java.repository;

import com.juno.concurrency.java.domain.entity.OptimisticMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptimisticMemberRepository extends JpaRepository<OptimisticMemberEntity, Long> {
}
