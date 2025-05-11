package com.ecms.repository;

import com.ecms.entity.ForecastFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastFileRepository extends JpaRepository<ForecastFile, Integer> {

    ForecastFile findTopByMerchantIdOrderByCreatedAtDesc(Integer merchantId);
}
