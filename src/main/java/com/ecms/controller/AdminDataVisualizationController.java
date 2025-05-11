package com.ecms.controller;

import com.ecms.service.AdminDataVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/data-visualization")
@CrossOrigin(origins = "http://localhost:8082")
public class AdminDataVisualizationController {

    @Autowired
    private AdminDataVisualizationService adminDataVisualizationService;

    /**
     * 获取平台销售概览数据
     * @return 销售概览数据
     */
    @GetMapping("/sales-overview")
    public ResponseEntity<Map<String, Object>> getPlatformSalesOverview() {
        try {
            Map<String, Object> data = adminDataVisualizationService.getPlatformSalesOverview();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取平台销售趋势数据
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 销售趋势数据
     */
    @GetMapping("/sales-trend")
    public ResponseEntity<Map<String, Object>> getPlatformSalesTrend(
            @RequestParam(defaultValue = "monthly") String period) {
        try {
            Map<String, Object> data = adminDataVisualizationService.getPlatformSalesTrend(period);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取商家销售排行数据
     * @param limit 限制数量
     * @return 商家销售排行数据
     */
    @GetMapping("/merchant-ranking")
    public ResponseEntity<Map<String, Object>> getMerchantSalesRanking(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> data = adminDataVisualizationService.getMerchantSalesRanking(limit);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取产品销售排行数据
     * @param limit 限制数量
     * @return 产品销售排行数据
     */
    @GetMapping("/product-ranking")
    public ResponseEntity<Map<String, Object>> getProductSalesRanking(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> data = adminDataVisualizationService.getProductSalesRanking(limit);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取区域销售分布数据
     * @return 区域销售分布数据
     */
    @GetMapping("/regional-distribution")
    public ResponseEntity<Map<String, Object>> getRegionalSalesDistribution() {
        try {
            Map<String, Object> data = adminDataVisualizationService.getRegionalSalesDistribution();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取订单状态分布数据
     * @return 订单状态分布数据
     */
    @GetMapping("/order-status")
    public ResponseEntity<Map<String, Object>> getOrderStatusDistribution() {
        try {
            Map<String, Object> data = adminDataVisualizationService.getOrderStatusDistribution();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取平台统计数据（所有图表）
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 平台统计数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getPlatformStatistics(
            @RequestParam(defaultValue = "monthly") String period) {
        try {
            Map<String, Object> data = adminDataVisualizationService.getPlatformStatistics(period);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
