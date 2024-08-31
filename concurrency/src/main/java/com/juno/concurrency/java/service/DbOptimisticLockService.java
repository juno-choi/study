package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.OptimisticMemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import com.juno.concurrency.java.repository.OptimisticMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbOptimisticLockService {
    private final OptimisticMemberRepository optimisticMemberRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public void post(RequestDto requestDto) {
        Optional<OptimisticMemberEntity> findOptimistic = optimisticMemberRepository.findById(1L);
        if (findOptimistic.isEmpty()) {
            throw new RuntimeException("no data");
        }
        OptimisticMemberEntity optimisticMemberEntity = findOptimistic.get();
        int version = optimisticMemberEntity.getVersion();
        if (version > 30) {
            throw new RuntimeException("30명 초과");
        }
        optimisticMemberEntity.setNumber(version);
        optimisticMemberEntity.setVersion(++version);
        optimisticMemberRepository.save(optimisticMemberEntity);

        memberRepository.save(requestDto.toModel());
    }
}
