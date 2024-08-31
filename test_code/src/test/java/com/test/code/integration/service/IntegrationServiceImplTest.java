package com.test.code.integration.service;

import com.test.code.integration.entity.IntegrationEntity;
import com.test.code.integration.repository.IntegrationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class IntegrationServiceImplTest {
    @Autowired
    IntegrationServiceImpl integrationService;

    @Autowired
    IntegrationRepository integrationRepository;

    @Test
    @DisplayName("모든 hello를 가져오는데 성공")
    @Transactional
    public void success() {
        // given
        String testName1 = "test1";
        String testName2 = "test2";
        IntegrationEntity test1 = IntegrationEntity.builder()
                .name(testName1)
                .build();
        IntegrationEntity test2 = IntegrationEntity.builder()
                .name(testName2)
                .build();
        integrationRepository.save(test1);
        integrationRepository.save(test2);

        // when
        String allName = integrationService.getAllName();

        // then
        Assertions.assertThat(allName).contains(testName1);
        Assertions.assertThat(allName).contains(testName2);
    }

}