package com.ecms.service;

import com.ecms.entity.VisualizationData;
import com.ecms.repository.VisualizationDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class VisualizationDataService {

    @Autowired
    private VisualizationDataRepository visualizationDataRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取所有可视化数据
     */
    public List<VisualizationData> getAllVisualizationData() {
        return visualizationDataRepository.findAll();
    }

    /**
     * 根据ID获取可视化数据
     */
    public VisualizationData getVisualizationDataById(Integer id) {
        return visualizationDataRepository.findById(id).orElse(null);
    }

    /**
     * 根据商家ID获取可视化数据
     */
    public List<VisualizationData> getVisualizationDataByMerchantId(Integer merchantId) {
        return visualizationDataRepository.findByMerchantId(merchantId);
    }

    /**
     * 根据商家ID和图表类型获取可视化数据
     */
    public List<VisualizationData> getVisualizationDataByMerchantIdAndChartType(
            Integer merchantId, VisualizationData.ChartType chartType) {
        return visualizationDataRepository.findByMerchantIdAndChartType(merchantId, chartType);
    }

    /**
     * 创建可视化数据
     */
    public VisualizationData createVisualizationData(VisualizationData visualizationData) {
        return visualizationDataRepository.save(visualizationData);
    }

    /**
     * 创建可视化数据（使用Map）
     */
    public VisualizationData createVisualizationData(Integer merchantId,
                                                     VisualizationData.ChartType chartType,
                                                     Map<String, Object> config,
                                                     Map<String, Object> data) {
        VisualizationData visualizationData = new VisualizationData();
        visualizationData.setMerchantId(merchantId);
        visualizationData.setChartType(chartType);

        try {
            visualizationData.setDataConfig(objectMapper.writeValueAsString(config));
            visualizationData.setDataSnapshot(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }

        return visualizationDataRepository.save(visualizationData);
    }

    /**
     * 更新可视化数据
     */
    public VisualizationData updateVisualizationData(Integer id, VisualizationData visualizationData) {
        if (visualizationDataRepository.existsById(id)) {
            visualizationData.setDataId(id);
            return visualizationDataRepository.save(visualizationData);
        }
        return null;
    }

    /**
     * 删除可视化数据
     */
    public void deleteVisualizationData(Integer id) {
        visualizationDataRepository.deleteById(id);
    }

    /**
     * 删除商家的所有可视化数据
     */
    @Transactional
    public void deleteVisualizationDataByMerchantId(Integer merchantId) {
        visualizationDataRepository.deleteByMerchantId(merchantId);
    }

    /**
     * 将JSON字符串转换为Map
     */
    public Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败", e);
        }
    }
}
