package com.ecms.repository;

import com.ecms.entity.SalesForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售预测仓库接口
 */
@Repository
public interface SalesForecastRepository extends JpaRepository<SalesForecast, Integer> {

    /**
     * 根据商家ID查找所有销售预测
     * @param merchantId 商家ID
     * @return 销售预测列表
     */
    List<SalesForecast> findByMerchantId(Integer merchantId);

    /**
     * 根据商家ID和产品ID查找销售预测
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @return 销售预测列表
     */
    List<SalesForecast> findByMerchantIdAndProductId(Integer merchantId, Integer productId);

    /**
     * 根据日期范围查找销售预测
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售预测列表
     */
    List<SalesForecast> findByForecastDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据商家ID和日期范围查找销售预测
     * @param merchantId 商家ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售预测列表
     */
    List<SalesForecast> findByMerchantIdAndForecastDateBetween(Integer merchantId, LocalDateTime startDate, LocalDateTime endDate);
}
