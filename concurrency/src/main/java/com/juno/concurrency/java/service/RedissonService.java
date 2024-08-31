package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.MemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedissonService {
    private final RedissonClient redissonClient;
    private final MemberRepository memberRepository;

    @Transactional
    public void post(RequestDto requestDto) throws InterruptedException {
        // redis lock
        String key = "lock:user:event";
        RLock lock = redissonClient.getLock(key);

        try {
            boolean isLock = lock.tryLock(3, 3, TimeUnit.SECONDS);
            if (! isLock) {
                throw new RuntimeException("lock fail");
            }

            List<MemberEntity> all = memberRepository.findAll();
            // 선착순 30명
            if (all.size() >= 30) {
                return ;
            }
            memberRepository.save(requestDto.toModel());
        } finally {
            lock.unlock();
        }
        // redis unlock
    }
}
