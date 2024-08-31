package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import com.juno.concurrency.java.domain.entity.MemberEntity;
import com.juno.concurrency.java.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LettuceService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    @Transactional
    public void post(RequestDto requestDto) throws InterruptedException {
        // redis lock
        String key = "lock:user:event";
        try {
            while (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofMillis(3_000)))) {
                Thread.sleep(10);
            }
            List<MemberEntity> all = memberRepository.findAll();
            // 선착순 30명
            if (all.size() >= 30) {
                return ;
            }
            memberRepository.save(requestDto.toModel());
        } finally {
            // redis unlock
            redisTemplate.delete(key);
        }
    }
}
