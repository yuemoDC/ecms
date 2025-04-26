package com.ecms.repository;

import com.ecms.entity.SalesPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesPredictionRepository extends JpaRepository<SalesPrediction, Integer> {

    /**
     * 根据商家ID查找销售预测
     * @param merchantId 商家ID
     * @return 销售预测列表
     */
    List<SalesPrediction> findByMerchantId(Integer merchantId);

    /**
     * 根据商家ID和预测日期查找销售预测
     * @param merchantId 商家ID
     * @param forecastDate 预测日期
     * @return 销售预测列表
     */
    List<SalesPrediction> findByMerchantIdAndForecastDate(Integer merchantId, LocalDate forecastDate);

    /**
     * 根据商家ID和预测日期范围查找销售预测
     * @param merchantId 商家ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售预测列表
     */
    List<SalesPrediction> findByMerchantIdAndForecastDateBetween(
            Integer merchantId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据商家ID和产品ID查找销售预测
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @return 销售预测列表
     */
    List<SalesPrediction> findByMerchantIdAndProductId(Integer merchantId, Integer productId);

    /**
     * 删除指定商家的所有销售预测
     * @param merchantId 商家ID
     */
    @Transactional
    void deleteByMerchantId(Integer merchantId);

    /**
     * 删除指定商家和产品的所有销售预测
     * @param merchantId 商家ID
     * @param productId 产品ID
     */
    @Transactional
    void deleteByMerchantIdAndProductId(Integer merchantId, Integer productId);
}
