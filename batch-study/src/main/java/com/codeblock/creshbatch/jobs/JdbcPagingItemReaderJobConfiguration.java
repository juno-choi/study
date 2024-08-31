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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcPagingItemReaderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception{
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")     //jdbcPagingItemReaderJob 이름으로 Job 생성
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() throws Exception{
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")   //jdbcPagingItemReaderStep 이름으로 Step 생성
                .<Pay, Pay>chunk(chunkSize)
                .reader(jdbcPagingItemReader())
                .writer(jdbcPagingItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Pay> jdbcPagingItemReader() throws Exception{
        Map<String, Object> param = new HashMap<>();    //parameter 생성
        param.put("amount", 2000);

        return new JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .queryProvider(createQueryProvider())   //각 DB에 정의된 provider를 사용해도 되지만 spring에서 적용 가능한 더 범용성 있는 코드 작성
                .parameterValues(param)                 //parameter
                .name("jdbcPagingItemReader")
                .build();
    }

    private ItemWriter<Pay> jdbcPagingItemWriter() {    //writer 실행
        return list -> {
            for(Pay pay : list){
                log.info("Current Pay={}",pay);
            }
        };
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);    //db에 맞는 pagingProvider 자동 선택
        queryProvider.setSelectClause("id, amount, tx_name, tx_date_time"); //select 문
        queryProvider.setFromClause("from pay");                            //from 절
        queryProvider.setWhereClause("where amount >= :amount");            //where 절

        Map<String, Order> sortKey = new HashMap<>(1);          //sort 정의
        sortKey.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKey);

        return queryProvider.getObject();   //결과값 반환
    }
}
