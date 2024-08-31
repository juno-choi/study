package com.juno.concurrency.java.service;

import com.juno.concurrency.java.domain.dto.RequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
class LockServiceTest {
    @Autowired
    private LockService lockService;
    // 동시성 제어 테스트 코드
    @Test
    public void testConcurrentReservation() throws InterruptedException {
        int totalThreads = 100;
        int perThread = 1;
        CountDownLatch latch = new CountDownLatch(totalThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        for (int i = 0; i < totalThreads; i++) {
            final int finalI = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < perThread; j++) {
                        RequestDto request = RequestDto.builder()
                                .number(finalI)
                                .build();
                        lockService.post(request);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}