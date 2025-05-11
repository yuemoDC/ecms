package com.ecms.repository;

import com.ecms.entity.SalesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SalesDataRepository extends JpaRepository<SalesData, Integer> {

    // 根据商家ID查询
    List<SalesData> findByMerchantId(Integer merchantId);

    // 根据销售日期范围查询
    List<SalesData> findBySalesDateBetween(Date startDate, Date endDate);

}
