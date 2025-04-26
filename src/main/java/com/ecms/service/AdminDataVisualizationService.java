package com.ecms.service;

import com.ecms.entity.Merchant;
import com.ecms.entity.Order;
import com.ecms.entity.Product;
import com.ecms.repository.MerchantRepository;
import com.ecms.repository.OrderRepository;
import com.ecms.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDataVisualizationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取平台总体销售概览数据
     * @return 销售概览数据
     */
    public Map<String, Object> getPlatformSalesOverview() {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 计算总销售额
        double totalSales = allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 计算总订单数
        int totalOrders = allOrders.size();

        // 计算平均订单价值
        double avgOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

        // 按订单状态分组
        Map<String, Long> statusCounts = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getOrderStatus, Collectors.counting()));

        // 计算最近30天的销售额
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        double recentSales = allOrders.stream()
                .filter(order -> order.getCreatedAt().isAfter(thirtyDaysAgo))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 计算最近30天的订单数
        long recentOrders = allOrders.stream()
                .filter(order -> order.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        // 计算商家数量
        long merchantCount = merchantRepository.count();

        // 计算活跃商家数量（有订单的商家）
        Set<Integer> activeMerchantIds = allOrders.stream()
                .map(Order::getMerchantId)
                .collect(Collectors.toSet());
        long activeMerchantCount = activeMerchantIds.size();

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("totalOrders", totalOrders);
        result.put("avgOrderValue", avgOrderValue);
        result.put("pendingOrders", statusCounts.getOrDefault("pending", 0L));
        result.put("completedOrders", statusCounts.getOrDefault("completed", 0L));
        result.put("shippedOrders", statusCounts.getOrDefault("shipped", 0L));
        result.put("recentSales", recentSales);
        result.put("recentOrders", recentOrders);
        result.put("merchantCount", merchantCount);
        result.put("activeMerchantCount", activeMerchantCount);

        return result;
    }

    /**
     * 获取平台销售趋势数据
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 销售趋势数据
     */
    public Map<String, Object> getPlatformSalesTrend(String period) {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 按时间周期分组
        Map<String, Double> salesByPeriod = groupOrdersByPeriod(allOrders, period);

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("title", getPeriodTitle(period) + "平台销售趋势");
        result.put("xAxis", new ArrayList<>(salesByPeriod.keySet()));
        result.put("series", new ArrayList<>(salesByPeriod.values()));

        return result;
    }

    /**
     * 获取商家销售排行数据
     * @param limit 限制数量
     * @return 商家销售排行数据
     */
    public Map<String, Object> getMerchantSalesRanking(int limit) {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 获取所有商家
        List<Merchant> merchants = merchantRepository.findAll();

        // 创建商家ID到名称的映射
        Map<Integer, String> merchantIdToName = new HashMap<>();
        for (Merchant merchant : merchants) {
            merchantIdToName.put(merchant.getMerchantId(),
                    merchant.getMerchantName() != null ? merchant.getMerchantName() : "商家ID-" + merchant.getMerchantId());
        }

        // 按商家分组计算销售额
        Map<Integer, Double> salesByMerchantId = new HashMap<>();
        for (Order order : allOrders) {
            Integer merchantId = order.getMerchantId();
            salesByMerchantId.put(merchantId,
                    salesByMerchantId.getOrDefault(merchantId, 0.0) + order.getTotalAmount());
        }

        // 转换为排序列表
        List<Map.Entry<Integer, Double>> sortedSales = new ArrayList<>(salesByMerchantId.entrySet());
        sortedSales.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());

        // 限制数量
        if (sortedSales.size() > limit) {
            sortedSales = sortedSales.subList(0, limit);
        }

        // 创建结果数据
        List<String> merchantNames = new ArrayList<>();
        List<Double> salesValues = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : sortedSales) {
            Integer merchantId = entry.getKey();
            String merchantName = merchantIdToName.getOrDefault(merchantId, "未知商家");
            merchantNames.add(merchantName);
            salesValues.add(entry.getValue());
        }

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("title", "商家销售排行");
        result.put("merchantNames", merchantNames);
        result.put("salesValues", salesValues);

        return result;
    }

    /**
     * 获取产品销售排行数据
     * @param limit 限制数量
     * @return 产品销售排行数据
     */
    public Map<String, Object> getProductSalesRanking(int limit) {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 获取所有产品
        List<Product> products = productRepository.findAll();

        // 创建产品ID到名称的映射
        Map<Integer, String> productIdToName = new HashMap<>();
        for (Product product : products) {
            productIdToName.put(product.getProductId(), product.getProductName());
        }

        // 按产品统计销售额
        Map<Integer, Double> salesByProductId = new HashMap<>();
        for (Order order : allOrders) {
            Integer productId = order.getProductId();
            salesByProductId.put(productId,
                    salesByProductId.getOrDefault(productId, 0.0) + order.getTotalAmount());
        }

        // 转换为排序列表
        List<Map.Entry<Integer, Double>> sortedSales = new ArrayList<>(salesByProductId.entrySet());
        sortedSales.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());

        // 限制数量
        if (sortedSales.size() > limit) {
            sortedSales = sortedSales.subList(0, limit);
        }

        // 创建结果数据
        List<String> productNames = new ArrayList<>();
        List<Double> salesValues = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : sortedSales) {
            Integer productId = entry.getKey();
            String productName = productIdToName.getOrDefault(productId, "未知产品");
            productNames.add(productName);
            salesValues.add(entry.getValue());
        }

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("title", "产品销售排行");
        result.put("productNames", productNames);
        result.put("salesValues", salesValues);

        return result;
    }

    /**
     * 获取区域销售分布数据
     * @return 区域销售分布数据
     */
    public Map<String, Object> getRegionalSalesDistribution() {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 按区域统计销售额
        Map<String, Double> salesByRegion = new HashMap<>();
        for (Order order : allOrders) {
            String region = order.getRegion() != null ? order.getRegion() : "未知区域";
            salesByRegion.put(region,
                    salesByRegion.getOrDefault(region, 0.0) + order.getTotalAmount());
        }

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("title", "区域销售分布");
        result.put("regions", new ArrayList<>(salesByRegion.keySet()));
        result.put("values", new ArrayList<>(salesByRegion.values()));

        return result;
    }

    /**
     * 获取订单状态分布数据
     * @return 订单状态分布数据
     */
    public Map<String, Object> getOrderStatusDistribution() {
        // 获取所有订单
        List<Order> allOrders = orderRepository.findAll();

        // 按订单状态统计数量
        Map<String, Integer> countByStatus = new HashMap<>();
        for (Order order : allOrders) {
            String status = order.getOrderStatus();
            countByStatus.put(getStatusDisplayName(status),
                    countByStatus.getOrDefault(getStatusDisplayName(status), 0) + 1);
        }

        // 创建饼图数据
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countByStatus.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieData.add(item);
        }

        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("title", "订单状态分布");
        result.put("data", pieData);

        return result;
    }

    /**
     * 获取平台总体统计数据（所有图表）
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 平台统计数据
     */
    public Map<String, Object> getPlatformStatistics(String period) {
        Map<String, Object> result = new HashMap<>();

        // 销售概览
        result.put("salesOverview", getPlatformSalesOverview());

        // 销售趋势
        result.put("salesTrend", getPlatformSalesTrend(period));

        // 商家销售排行
        result.put("merchantRanking", getMerchantSalesRanking(10));

        // 产品销售排行
        result.put("productRanking", getProductSalesRanking(10));

        // 区域销售分布
        result.put("regionalDistribution", getRegionalSalesDistribution());

        // 订单状态分布
        result.put("orderStatusDistribution", getOrderStatusDistribution());

        return result;
    }

    /**
     * 按时间周期分组订单
     * @param orders 订单列表
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 按时间周期分组的销售额
     */
    private Map<String, Double> groupOrdersByPeriod(List<Order> orders, String period) {
        Map<String, Double> salesByPeriod = new TreeMap<>();
        DateTimeFormatter formatter;

        switch (period.toLowerCase()) {
            case "daily":
                formatter = DateTimeFormatter.ofPattern("MM-dd");
                break;
            case "weekly":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'w");
                break;
            case "monthly":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
        }

        for (Order order : orders) {
            LocalDateTime orderDate = order.getCreatedAt();
            String periodKey = orderDate.format(formatter);
            salesByPeriod.put(periodKey,
                    salesByPeriod.getOrDefault(periodKey, 0.0) + order.getTotalAmount());
        }

        return salesByPeriod;
    }

    /**
     * 获取周期标题
     */
    private String getPeriodTitle(String period) {
        switch (period.toLowerCase()) {
            case "daily":
                return "每日";
            case "weekly":
                return "每周";
            case "monthly":
                return "每月";
            default:
                return "每月";
        }
    }

    /**
     * 获取订单状态显示名称
     */
    private String getStatusDisplayName(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "待处理";
            case "shipped":
                return "已发货";
            case "completed":
                return "已完成";
            case "refunded":
                return "已退款";
            default:
                return status;
        }
    }
}
