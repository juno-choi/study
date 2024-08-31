package com.codeblock.creshbatch.jobs;

import com.codeblock.creshbatch.config.TestBatchConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes={Calculator.class, TestBatchConfig.class})
class CalculatorTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void 계산기테스트() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("version", LocalDateTime.now().toString())
                .addDouble("value1", 2.0)
                .addDouble("value2", 3.4)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        // JobParameter와 함께 Job을 실행
        // 해당 Job의 결과는 JobExecution에 담겨 반환
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        // 성공적으로 배치가 수행되었는지 검증
    }
}