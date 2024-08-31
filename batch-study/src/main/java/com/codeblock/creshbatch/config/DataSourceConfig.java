package com.codeblock.creshbatch.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * data source 다중화를 위한 db 설정 파일
 */

@Configuration
public class DataSourceConfig {

    //===============================
    //(1) batch db 설정 (=기본 primary 설정)
    @Value("${db.batch.driver-class-name}")
    private String defaultDriverClassName;
    @Value("${db.batch.jdbc-url}")
    private String defaultJdbcUrl;
    @Value("${db.batch.username}")
    private String defaultJdbcUserName;
    @Value("${db.batch.password}")
    private String defaultJdbcPassword;
    //batch db 설정 종료

    //(2) service db 설정
    @Value("${db.service.driver-class-name}")
    private String serviceDriverClassName;
    @Value("${db.service.jdbc-url}")
    private String serviceJdbcUrl;
    @Value("${db.service.username}")
    private String serviceJdbcUserName;
    @Value("${db.service.password}")
    private String serviceJdbcPassword;
    //service db 설정 종료
    //===============================
    @Primary
    @Bean(name = "defaultDb")
    public DataSource defaultDataSource(){
        return getDataSource(defaultDriverClassName, defaultJdbcUrl, defaultJdbcUserName, defaultJdbcPassword);
    }

    @Bean(name = "serviceDb")
    public DataSource serviceDataSource(){
        return getDataSource(serviceDriverClassName, serviceJdbcUrl, serviceJdbcUserName, serviceJdbcPassword);
    }
    //(3) DataSource 정보 세팅 method
    private DataSource getDataSource(String driverClassName, String jdbcUrl, String jdbcUserName, String jdbcPassword) {
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(jdbcUserName)
                .password(jdbcPassword)
                .type(HikariDataSource.class)
                .build();
    }
}