package com.codeblock.creshbatch.jobs;

import com.codeblock.creshbatch.config.TestBatchConfig;
import com.codeblock.creshbatch.domain.dao.Pay;
import com.codeblock.creshbatch.repository.PayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)  //(0) Spring framework에 junit test를 확장하여 실행하도록 선언
@SpringBatchTest    //(1) batch에 필요한 jobLauncherTestUtils, jobRepositoryTestUtils 등의 DI 적용
@SpringBootTest(classes = {TestBatchConfig.class, SuccessStateJob.class, PayRepository.class})  //(2) Spring을 시작시키며 Test를 실행하는데 classes 옵션으로 해당 테스트가 진행될때 applicationContext에 등록할 class들을 정의함. > 여기에 작성하지 않으면 테스트 실행시 DI가 되지 않아 class를 찾을 수 없다고 나옴
class SuccessStateJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;  //(3) batch test용 library

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;  //(4) 3과 동일

    @Autowired
    private PayRepository payRepository;    //(5) 개인적으로 정의한 repository

    @Test
    void 테스트1() throws Exception{

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void 같은조건을읽고_업데이트() throws Exception{
        //given
        for(long i=0; i<50; i++){
            payRepository.save(new Pay(1000L, "테스트상품",LocalDateTime.now().toString(),"false")); //(6) 상품 50개 등록
        }
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();   //(7) job 실행
        //then
        assertThat(payRepository.findAllByState().size()).isEqualTo(54);    //(8) 결과 확인
    }
}