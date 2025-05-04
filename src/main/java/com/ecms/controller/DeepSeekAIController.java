package com.ecms.controller;

import com.ecms.service.ARIMASalesPredictionService;
import com.ecms.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:8081")  // 确保与前端一致的跨域设置
public class DeepSeekAIController {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekAIController.class);

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private ARIMASalesPredictionService arimaService;

    /**
     * 获取商家整体AI建议
     *
     * @param merchantId 商家ID
     * @return AI建议
     */
    @GetMapping("/suggestion/{merchantId}")
    public ResponseEntity<Map<String, Object>> getMerchantSuggestion(@PathVariable Integer merchantId) {
        logger.info("接收到商家AI建议请求，商家ID={}", merchantId);

        try {
            // 获取销售预测数据
            Map<String, Object> predictionResult = arimaService.predictSalesTrend(merchantId);

            // 检查是否有错误消息
            if (predictionResult.containsKey("message")) {
                logger.warn("商家ID={}无法生成预测：{}", merchantId, predictionResult.get("message"));
                Map<String, Object> response = new HashMap<>();
                response.put("merchantId", merchantId);
                response.put("suggestion", "无法为该商家生成AI建议：" + predictionResult.get("message"));
                return ResponseEntity.ok(response);
            }

            // 从预测结果中提取日期和预测值
            List<String> dates = (List<String>) predictionResult.get("dates");
            List<Double> predictions = (List<Double>) predictionResult.get("predictions");

            if (dates == null || predictions == null || dates.isEmpty() || predictions.isEmpty()) {
                logger.warn("商家ID={}的预测数据为空", merchantId);
                Map<String, Object> response = new HashMap<>();
                response.put("merchantId", merchantId);
                response.put("suggestion", "无法生成建议，预测数据为空");
                return ResponseEntity.ok(response);
            }

            // 调用DeepSeek服务生成建议
            String suggestion = deepSeekService.generateSuggestionFromPredictionData(dates, predictions);

            // 组装响应
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("suggestion", suggestion);

            logger.info("成功生成商家ID={}的AI建议", merchantId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("生成商家AI建议失败，商家ID={}", merchantId, e);

            // 返回友好的错误消息
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("suggestion", "生成AI建议时发生错误，请稍后重试");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取特定产品的AI建议
     *
     * @param merchantId 商家ID
     * @param productId  产品ID
     * @param days       预测天数
     * @return AI建议
     */
    @GetMapping("/suggestion/{merchantId}/{productId}/{days}")
    public ResponseEntity<Map<String, Object>> getProductSuggestion(
            @PathVariable Integer merchantId,
            @PathVariable Integer productId,
            @PathVariable int days) {

        logger.info("接收到产品AI建议请求，商家ID={}, 产品ID={}, 天数={}", merchantId, productId, days);

        try {
            // 获取产品销售预测数据
            Map<String, Object> predictionResult = arimaService.predictProductSales(merchantId, productId, days);

            // 从预测结果中提取日期和预测值
            List<String> dates = (List<String>) predictionResult.get("dates");
            List<Double> predictions = (List<Double>) predictionResult.get("predictions");

            if (dates == null || predictions == null || dates.isEmpty() || predictions.isEmpty()) {
                logger.warn("商家ID={}, 产品ID={}的预测数据为空", merchantId, productId);
                Map<String, Object> response = new HashMap<>();
                response.put("merchantId", merchantId);
                response.put("productId", productId);
                response.put("suggestion", "无法生成建议，预测数据为空或产品不存在");
                return ResponseEntity.ok(response);
            }

            // 调用DeepSeek服务生成建议
            String suggestion = deepSeekService.generateSuggestionFromPredictionData(dates, predictions);

            // 组装响应
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("productId", productId);
            response.put("suggestion", suggestion);

            logger.info("成功生成商家ID={}, 产品ID={}的AI建议", merchantId, productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("生成产品AI建议失败，商家ID={}, 产品ID={}", merchantId, productId, e);

            // 返回友好的错误消息
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("productId", productId);
            response.put("suggestion", "生成AI建议时发生错误，请稍后重试");
            return ResponseEntity.ok(response);
        }
    }



}
