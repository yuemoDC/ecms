package com.ecms.controller;

import com.ecms.entity.SalesPrediction;
import com.ecms.service.ARIMASalesPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:8081")  // 允许跨域访问
public class AIController {

    @Autowired
    private ARIMASalesPredictionService arimaSalesPredictionService;

    /**
     * 获取商家的销售预测数据（所有产品）
     * @param merchantId 商家ID
     * @return 销售预测数据
     */
    @GetMapping("/sales-prediction/{merchantId}")
    public ResponseEntity<Map<String, Object>> getSalesPrediction(@PathVariable Integer merchantId) {
        Map<String, Object> predictions = arimaSalesPredictionService.predictSalesTrend(merchantId);

        // 如果没有找到产品，返回提示消息
        if (predictions.containsKey("message")) {
            return ResponseEntity.badRequest().body(predictions);
        }

        return ResponseEntity.ok(predictions);
    }

    /**
     * 获取商家特定产品的销售预测数据
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @return 销售预测数据
     */
    @GetMapping("/product-sales-prediction/{merchantId}/{productId}")
    public ResponseEntity<Map<String, Object>> getProductSalesPrediction(
            @PathVariable Integer merchantId,
            @PathVariable Integer productId) {

        // 获取指定产品的销售预测
        Map<String, Object> predictions = arimaSalesPredictionService.predictProductSales(merchantId, productId, 30);

        return ResponseEntity.ok(predictions);
    }

    /**
     * 获取商家销售预测数据（按照产品ID和预测天数）
     * @param merchantId 商家ID
     * @param productId 产品ID
     * @param daysToPredict 预测天数
     * @return 销售预测数据
     */
    @GetMapping("/sales-prediction-by-product/{merchantId}/{productId}/{daysToPredict}")
    public ResponseEntity<Map<String, Object>> getSalesPredictionByProduct(
            @PathVariable Integer merchantId,
            @PathVariable Integer productId,
            @PathVariable int daysToPredict) {

        // 获取特定产品的销售预测
        Map<String, Object> predictions = arimaSalesPredictionService.predictProductSales(merchantId, productId, daysToPredict);

        return ResponseEntity.ok(predictions);
    }






    /**
     * 获取多个商家的总体销售预测对比
     * @param merchantIds 商家ID列表
     * @return 销售预测对比数据
     */
    @PostMapping("/multi-merchant-prediction")
    public ResponseEntity<List<Map<String, Object>>> getMultiMerchantPrediction(
            @RequestBody List<Integer> merchantIds) {

        if (merchantIds == null || merchantIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Map<String, Object>> predictions = arimaSalesPredictionService.predictSalesTrendForMerchants(merchantIds);
        return ResponseEntity.ok(predictions);
    }

    /**
     * 获取多个商家特定产品的销售预测对比
     * @param request 包含商家ID列表、产品ID和预测天数的请求
     * @return 销售预测对比数据
     */
    @PostMapping("/multi-merchant-product-prediction")
    public ResponseEntity<List<Map<String, Object>>> getMultiMerchantProductPrediction(
            @RequestBody MultiMerchantPredictionRequest request) {

        if (request == null || request.getMerchantIds() == null || request.getMerchantIds().isEmpty() || request.getProductId() == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        int daysToPredict = request.getDaysToPredict() > 0 ? request.getDaysToPredict() : 30;

        List<Map<String, Object>> predictions = arimaSalesPredictionService.predictProductSalesForMerchants(
                request.getMerchantIds(), request.getProductId(), daysToPredict);

        return ResponseEntity.ok(predictions);
    }

    /**
     * 多商家预测请求对象
     */
    static class MultiMerchantPredictionRequest {
        private List<Integer> merchantIds;
        private Integer productId;
        private int daysToPredict;

        public List<Integer> getMerchantIds() {
            return merchantIds;
        }

        public void setMerchantIds(List<Integer> merchantIds) {
            this.merchantIds = merchantIds;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public int getDaysToPredict() {
            return daysToPredict;
        }

        public void setDaysToPredict(int daysToPredict) {
            this.daysToPredict = daysToPredict;
        }
    }
}
