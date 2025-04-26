package com.ecms.service;

import com.ecms.entity.SalesPrediction;
import com.ecms.repository.SalesPredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ARIMASalesPredictionService {

    @Autowired
    private SalesPredictionRepository salesPredictionRepository;

    /**
     * 预测商家所有产品的未来30天销售趋势
     * @param merchantId 商家ID
     * @return 销售预测数据
     */
    public Map<String, Object> predictSalesTrend(Integer merchantId) {
        // 获取商家所有产品的销售预测
        List<SalesPrediction> predictions = salesPredictionRepository.findByMerchantId(merchantId);

        // 如果没有找到预测数据，返回提示信息
        if (predictions.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("message", "No sales predictions found for this merchant.");
            emptyResult.put("dates", Collections.emptyList());
            emptyResult.put("predictions", Collections.emptyList());
            return emptyResult;
        }

        // 组装预测数据
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> predictedSales = new ArrayList<>();

        for (SalesPrediction prediction : predictions) {
            dates.add(prediction.getForecastDate().toString());
            predictedSales.add(prediction.getPredictedSales().doubleValue());
        }

        result.put("dates", dates);
        result.put("predictions", predictedSales);
        return result;
    }

    /**
     * 获取指定商家的销售预测（特定日期范围）
     * @param merchantId 商家ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售预测列表
     */
    public List<SalesPrediction> getSalesPredictionsByDateRange(Integer merchantId, LocalDate startDate, LocalDate endDate) {
        return salesPredictionRepository.findByMerchantIdAndForecastDateBetween(merchantId, startDate, endDate);
    }

    /**
     * 为特定产品生成销售预测
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @param daysToPredict 预测天数
     * @return 销售预测数据
     */
    public Map<String, Object> predictProductSales(Integer merchantId, Integer productId, int daysToPredict) {
        // 假设ARIMA模型已经计算了预测结果
        List<Double> predictions = generatePredictions(productId, daysToPredict);

        // 生成日期标签
        List<String> dateLabels = generateFutureDates(daysToPredict);

        // 保存产品预测数据
        saveProductPredictions(merchantId, productId, dateLabels, predictions);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("dates", dateLabels);
        result.put("predictions", predictions);
        return result;
    }

    /**
     * 根据产品ID和预测天数生成随机预测数据
     * @param productId 产品ID
     * @param daysToPredict 预测天数
     * @return 销售预测数据
     */
    private List<Double> generatePredictions(Integer productId, int daysToPredict) {
        List<Double> predictions = new ArrayList<>();
        double baseSales = 100.0 + Math.random() * 200.0;

        for (int i = 0; i < daysToPredict; i++) {
            double dailyPrediction = baseSales * (0.9 + Math.random() * 0.3);  // 随机生成每日预测
            predictions.add(dailyPrediction);
        }

        return predictions;
    }

    /**
     * 生成未来日期的标签
     * @param daysToPredict 预测天数
     * @return 日期标签
     */
    private List<String> generateFutureDates(int daysToPredict) {
        List<String> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= daysToPredict; i++) {
            LocalDate futureDate = currentDate.plusDays(i);
            dates.add(futureDate.toString());
        }

        return dates;
    }

    /**
     * 保存产品销售预测
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @param dates 日期
     * @param predictions 销售预测数据
     */
    private void saveProductPredictions(Integer merchantId, Integer productId, List<String> dates, List<Double> predictions) {
        for (int i = 0; i < dates.size(); i++) {
            SalesPrediction prediction = new SalesPrediction();
            prediction.setMerchantId(merchantId);
            prediction.setProductId(productId);
            prediction.setForecastDate(LocalDate.parse(dates.get(i)));
            prediction.setPredictedSales(BigDecimal.valueOf(predictions.get(i)));
            prediction.setCreatedAt(LocalDateTime.now());

            // 存储置信区间（简单例子）
            double lowerBound = predictions.get(i) * 0.85;
            double upperBound = predictions.get(i) * 1.15;
            String confidenceJson = String.format("{\"lower\": %.2f, \"upper\": %.2f}", lowerBound, upperBound);
            prediction.setConfidenceInterval(confidenceJson);

            salesPredictionRepository.save(prediction);
        }
    }











    /**
     * 获取多个商家相同产品的销售预测对比
     * @param merchantIds 商家ID列表
     * @param productId 产品ID
     * @param daysToPredict 预测天数
     * @return 多商家的销售预测数据
     */
    public List<Map<String, Object>> predictProductSalesForMerchants(List<Integer> merchantIds, Integer productId, int daysToPredict) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (Integer merchantId : merchantIds) {
            Map<String, Object> prediction = predictProductSales(merchantId, productId, daysToPredict);
            prediction.put("merchantId", merchantId);
            results.add(prediction);
        }

        return results;
    }

    /**
     * 获取多个商家的总体销售预测对比
     * @param merchantIds 商家ID列表
     * @return 多商家的销售预测数据
     */
    public List<Map<String, Object>> predictSalesTrendForMerchants(List<Integer> merchantIds) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (Integer merchantId : merchantIds) {
            Map<String, Object> prediction = predictSalesTrend(merchantId);

            // 如果预测数据不存在，生成随机数据进行演示
            if (prediction.containsKey("message")) {
                prediction = generateRandomPrediction(30);
            }

            prediction.put("merchantId", merchantId);
            results.add(prediction);
        }

        return results;
    }

    /**
     * 生成随机预测数据（用于演示）
     * @param daysToPredict 预测天数
     * @return 随机销售预测数据
     */
    private Map<String, Object> generateRandomPrediction(int daysToPredict) {
        List<String> dates = generateFutureDates(daysToPredict);
        List<Double> predictions = new ArrayList<>();
        double baseSales = 100.0 + Math.random() * 200.0;

        for (int i = 0; i < daysToPredict; i++) {
            double dailyPrediction = baseSales * (0.9 + Math.random() * 0.3);
            predictions.add(dailyPrediction);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("predictions", predictions);

        return result;
    }
}
