package com.juno.concurrency.java.repository;

import com.juno.concurrency.java.domain.entity.MemberEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select m from MemberEntity m where m.memberId >= 30")
    Optional<MemberEntity> findByMemberIdForLock();
}
