package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.MemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NormalService {
    private final MemberRepository memberRepository;

    @Transactional
    public void post(RequestDto requestDto) {
        List<MemberEntity> all = memberRepository.findAll();
        // 선착순 30명
        if (all.size() >= 30) {
            return ;
        }
        memberRepository.save(requestDto.toModel());
    }
}
