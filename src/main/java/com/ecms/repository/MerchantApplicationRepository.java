package com.ecms.repository;

import com.ecms.entity.MerchantApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantApplicationRepository extends JpaRepository<MerchantApplication, Integer> {
    List<MerchantApplication> findByUserId(Integer userId);
}
