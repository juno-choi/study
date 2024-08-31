package com.test.code.integration.service;

import com.test.code.integration.entity.IntegrationEntity;
import com.test.code.integration.repository.IntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntegrationServiceImpl implements IntegrationService {
    private final IntegrationRepository integrationRepository;
    @Override
    public String getAllName() {
        List<IntegrationEntity> allIntegration = integrationRepository.findAll();
        return allIntegration.stream().map(IntegrationEntity::getName).collect(Collectors.joining(","));
    }
}
