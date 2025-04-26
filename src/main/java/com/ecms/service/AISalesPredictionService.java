package com.ecms.service;

import com.ecms.entity.Order;
import com.ecms.entity.Product;
import com.ecms.entity.SalesPrediction;
import com.ecms.repository.OrderRepository;
import com.ecms.repository.ProductRepository;
import com.ecms.repository.SalesPredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AISalesPredictionService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SalesPredictionRepository salesPredictionRepository;

    @Autowired
    private ProductRepository productRepository;  // Ensure this is added!

    /**
     * 预测未来30天的销售趋势
     * @param merchantId 商家ID
     * @return 销售预测数据
     */
    public Map<String, Object> predictSalesTrend(Integer merchantId) {
        // 获取历史销售数据
        List<Order> historicalOrders = orderRepository.findByMerchantId(merchantId);

        // 按日期分组
        Map<String, Double> dailySales = groupOrdersByDate(historicalOrders);

        // 使用简单的移动平均线算法预测未来销售
        List<Double> salesValues = new ArrayList<>(dailySales.values());
        List<Double> predictions = generatePredictions(salesValues, 30);

        // 生成日期标签
        List<String> dateLabels = generateFutureDates(30);

        // 保存预测结果
        savePredictions(merchantId, dateLabels, predictions);

        // 返回预测结果
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dateLabels);
        result.put("predictions", predictions);
        return result;
    }

    private Map<String, Double> groupOrdersByDate(List<Order> orders) {
        Map<String, Double> dailySales = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Order order : orders) {
            String dateKey = order.getCreatedAt().format(formatter);
            dailySales.put(
                    dateKey,
                    dailySales.getOrDefault(dateKey, 0.0) + order.getTotalAmount()
            );
        }

        return dailySales;
    }

    private List<Double> generatePredictions(List<Double> historicalData, int daysToPredict) {
        List<Double> predictions = new ArrayList<>();

        // 简单的移动平均线算法 (可以替换为更复杂的算法)
        int windowSize = Math.min(7, historicalData.size());
        if (historicalData.size() < windowSize) {
            return predictions;
        }

        double sum = 0;
        for (int i = historicalData.size() - windowSize; i < historicalData.size(); i++) {
            sum += historicalData.get(i);
        }
        double movingAvg = sum / windowSize;

        // 生成预测数据
        for (int i = 0; i < daysToPredict; i++) {
            // 简单模型：添加一些随机波动
            double randomFactor = 0.9 + Math.random() * 0.2; // 0.9到1.1之间的随机值
            double prediction = movingAvg * randomFactor;
            predictions.add(prediction);
        }

        return predictions;
    }

    private List<String> generateFutureDates(int daysToPredict) {
        List<String> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i <= daysToPredict; i++) {
            LocalDate futureDate = currentDate.plusDays(i);
            dates.add(futureDate.format(formatter));
        }

        return dates;
    }

    private void savePredictions(Integer merchantId, List<String> dates, List<Double> predictions) {
        // 清除旧的预测
        salesPredictionRepository.deleteByMerchantId(merchantId);

        // 获取该商家的第一个产品ID (确保存在)
        List<Product> merchantProducts = productRepository.findByMerchantId(merchantId);  // Ensure productRepository is used correctly!
        if (merchantProducts.isEmpty()) {
            // 如果商家没有产品，无法保存预测
            return;
        }
        Integer firstProductId = merchantProducts.get(0).getProductId();

        // 保存新的预测
        for (int i = 0; i < dates.size(); i++) {
            SalesPrediction prediction = new SalesPrediction();
            prediction.setMerchantId(merchantId);
            prediction.setProductId(firstProductId); // 使用真实存在的产品ID
            prediction.setForecastDate(LocalDate.parse(dates.get(i)));
            prediction.setPredictedSales(BigDecimal.valueOf(predictions.get(i)));
            prediction.setCreatedAt(LocalDateTime.now());

            // 简单的置信区间设置
            double lowerBound = predictions.get(i) * 0.9;
            double upperBound = predictions.get(i) * 1.1;
            String confidenceJson = String.format("{\"lower\": %.2f, \"upper\": %.2f}", lowerBound, upperBound);
            prediction.setConfidenceInterval(confidenceJson);

            salesPredictionRepository.save(prediction);
        }
    }
}
