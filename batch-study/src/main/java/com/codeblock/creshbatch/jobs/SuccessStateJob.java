package com.codeblock.creshbatch.jobs;

import com.codeblock.creshbatch.domain.dao.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * pay table의 false 상태의 데이터들을 true로 변경하는 job
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SuccessStateJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Autowired
    @Qualifier("serviceDb")
    private DataSource dataSource;    //db

    private static final int chunkSize = 10;

    @Bean
    public Job SuccessStateJob() throws Exception{
        return jobBuilderFactory.get("successStateJob")
                .start(successStateStep())
                .build();
    }

    @Bean
    public Step successStateStep() throws Exception{
        return stepBuilderFactory.get("successStateStep")
                .<Pay, Pay>chunk(chunkSize)
                .reader(successStateReader())
                .processor(successStateProcessor())   //필요할 경우 데이터 처리
                .writer(successStateWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Pay> successStateReader() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("success_state","false");

        return new JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .queryProvider(createProvider())
                .parameterValues(param)
                .name("jdbcPagingItemReader")
                .build();
    }

    @Bean
    public PagingQueryProvider createProvider() throws Exception{
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("id, amount, tx_name, tx_date_time, success_state");
        provider.setFromClause("from pay");
        provider.setWhereClause("where success_state = :success_state");

        Map<String, Order> sortKey = new HashMap<>(1);
        sortKey.put("id", Order.ASCENDING);
        provider.setSortKeys(sortKey);

        return provider.getObject();
    }

    @Bean
    @StepScope
    public ItemProcessor<? super Pay, ? extends Pay> successStateProcessor() { //success_state를 true 처리
        return item -> {
            item.setSuccessState("true");
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<? super Pay> successStateWriter() {
        return new JdbcBatchItemWriterBuilder<Pay>()
                .dataSource(dataSource)
                .sql("update pay set success_state = :successState where id = :id")
                .beanMapped()
                .build();
    }
}
