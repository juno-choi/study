package com.codeblock.creshbatch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class BatchTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    @Qualifier("serviceDb")
    DataSource dataSource;

    @Test
    void bean_이름_조회(){
        String[] beans = applicationContext.getBeanDefinitionNames();
        for (String bean : beans) {
            System.out.println("bean = " + bean);
        }
    }

    @Test
    void dataSource_출력(){
        System.out.println("dataSource = "+dataSource.toString());
    }
}
