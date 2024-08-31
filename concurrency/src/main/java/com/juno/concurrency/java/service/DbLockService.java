package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.MemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbLockService {
    private final MemberRepository memberRepository;
    @Transactional
    public void post(RequestDto requestDto) {
        Optional<MemberEntity> findMember = memberRepository.findByMemberIdForLock();
        // 선착순 30명
        if (findMember.isPresent()) {
            return ;
        }
        memberRepository.save(requestDto.toModel());
    }
}
