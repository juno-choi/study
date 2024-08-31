package com.codeblock.creshbatch.jobs;

import com.codeblock.creshbatch.domain.dao.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcCursorItemReaderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcCursorItemReaderJob(){
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")     //jdbcCursorItemReaderJob 이름으로 job 생성
                .start(jdbcCursorItemReaderStep())  //step 실행
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")   //jdbcCursorItemReaderStep 이름으로 step 생성
                .<Pay, Pay>chunk(chunkSize)     //chunk size 지정 (=transaction 범위)
                .reader(jdbcCursorItemReader()) //reader 실행
                .writer(jdbcCursorItemWriter()) //writer 실행
                .build();
    }

    @Bean
    public ItemReader<? extends Pay> jdbcCursorItemReader() {       //itemReader 구현
        return new JdbcCursorItemReaderBuilder<Pay>()
                .fetchSize(chunkSize)       //db에서 읽어오는 데이터 양
                .dataSource(dataSource)     //db 설정
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))  //반환되는 데이터를 Pay 객체로 변환
                .sql("select id, amount, tx_name, tx_date_time from pay")
                .name("jdbcCursorItemReader")
                .build();
    }

    private ItemWriter<? super Pay> jdbcCursorItemWriter() {        //writer 구현
        return list -> {
            for(Pay pay : list){
                log.info("Current Pay={}",pay);
            }
        };
    }
}
