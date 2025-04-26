package com.ecms.repository;

import com.ecms.entity.VisualizationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisualizationDataRepository extends JpaRepository<VisualizationData, Integer>, JpaSpecificationExecutor<VisualizationData> {

    // 根据商家ID查询可视化数据
    List<VisualizationData> findByMerchantId(Integer merchantId);

    // 根据商家ID和图表类型查询可视化数据
    List<VisualizationData> findByMerchantIdAndChartType(Integer merchantId, VisualizationData.ChartType chartType);

    // 删除商家的所有可视化数据
    void deleteByMerchantId(Integer merchantId);
}
