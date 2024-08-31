package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.MemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LockService {
    private final MemberRepository memberRepository;
    @Transactional
    public  void post(RequestDto requestDto) {
        Lock lock = new ReentrantLock();

        try {
            lock.lock();
            List<MemberEntity> all = memberRepository.findAll();
            // 선착순 30명
            if (all.size() >= 30) {
                return ;
            }
        } finally {
            // 에러가 나던 어찌 됐던 무조건 lock 풀어야 함.
            lock.unlock();
        }

        memberRepository.save(requestDto.toModel());
    }
}
