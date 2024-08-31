package com.test.code.integration.repository;

import com.test.code.integration.entity.IntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, Long> {
}
