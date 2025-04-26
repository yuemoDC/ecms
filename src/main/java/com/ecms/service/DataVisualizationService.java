package com.ecms.service;

import com.ecms.entity.Order;
import com.ecms.entity.Product;
import com.ecms.entity.VisualizationData;
import com.ecms.repository.OrderRepository;
import com.ecms.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataVisualizationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VisualizationDataService visualizationDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成销售趋势可视化数据
     *
     * @param merchantId 商家ID
     * @param period 统计周期 (daily, weekly, monthly)
     * @return 销售趋势可视化数据
     */
    public VisualizationData generateSalesTrendVisualization(Integer merchantId, String period) {
        // 获取商家订单
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // 按周期分组订单
        Map<String, Double> salesByPeriod = groupOrdersByPeriod(orders, period);

        // 转换为Map<String, Object>
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("xAxis", new ArrayList<>(salesByPeriod.keySet()));
        dataMap.put("series", new ArrayList<>(salesByPeriod.values()));

        // 创建图表配置
        Map<String, Object> chartConfig = new HashMap<>();
        chartConfig.put("title", getPeriodTitle(period) + "销售趋势");
        chartConfig.put("xAxis", "时间");
        chartConfig.put("yAxis", "销售额");

        // 创建可视化数据
        return visualizationDataService.createVisualizationData(
                merchantId,
                VisualizationData.ChartType.line,
                chartConfig,
                dataMap);
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
     * 生成产品销售分布可视化数据
     *
     * @param merchantId 商家ID
     * @return 产品销售分布可视化数据
     */
    public VisualizationData generateProductSalesDistribution(Integer merchantId) {
        // 获取商家订单
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // 获取商家产品
        List<Product> products = productRepository.findByMerchantId(merchantId);

        // 创建产品ID到产品名称的映射
        Map<Integer, String> productIdToName = new HashMap<>();
        for (Product product : products) {
            productIdToName.put(product.getProductId(), product.getProductName());
        }

        // 按产品统计销售额
        Map<String, Double> salesByProduct = new HashMap<>();
        for (Order order : orders) {
            String productName = productIdToName.getOrDefault(order.getProductId(), "未知产品");
            salesByProduct.put(productName, salesByProduct.getOrDefault(productName, 0.0) + order.getTotalAmount());
        }

        // 创建饼图数据
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Double> entry : salesByProduct.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieData.add(item);
        }

        // 创建图表配置
        Map<String, Object> chartConfig = new HashMap<>();
        chartConfig.put("title", "产品销售分布");
        chartConfig.put("radius", "60%");

        // 创建可视化数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", pieData);

        return visualizationDataService.createVisualizationData(
                merchantId,
                VisualizationData.ChartType.pie,
                chartConfig,
                dataMap);
    }

    /**
     * 生成区域销售分布可视化数据
     *
     * @param merchantId 商家ID
     * @return 区域销售分布可视化数据
     */
    public VisualizationData generateRegionalSalesDistribution(Integer merchantId) {
        // 获取商家订单
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // 按区域统计销售额
        Map<String, Double> salesByRegion = new HashMap<>();
        for (Order order : orders) {
            String region = order.getRegion() != null ? order.getRegion() : "未知区域";
            salesByRegion.put(region, salesByRegion.getOrDefault(region, 0.0) + order.getTotalAmount());
        }

        // 转换为Map<String, Object>
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("regions", new ArrayList<>(salesByRegion.keySet()));
        dataMap.put("values", new ArrayList<>(salesByRegion.values()));

        // 创建图表配置
        Map<String, Object> chartConfig = new HashMap<>();
        chartConfig.put("title", "区域销售分布");

        // 创建可视化数据
        return visualizationDataService.createVisualizationData(
                merchantId,
                VisualizationData.ChartType.bar,
                chartConfig,
                dataMap);
    }



    /**
     * 生成订单状态分布可视化数据
     *
     * @param merchantId 商家ID
     * @return 订单状态分布可视化数据
     */
    public VisualizationData generateOrderStatusDistribution(Integer merchantId) {
        // 获取商家订单
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // 按订单状态统计数量
        Map<String, Integer> countByStatus = new HashMap<>();
        for (Order order : orders) {
            String status = order.getOrderStatus();
            countByStatus.put(getStatusDisplayName(status), countByStatus.getOrDefault(getStatusDisplayName(status), 0) + 1);
        }

        // 创建饼图数据
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countByStatus.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieData.add(item);
        }

        // 创建图表配置
        Map<String, Object> chartConfig = new HashMap<>();
        chartConfig.put("title", "订单状态分布");

        // 创建可视化数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", pieData);

        return visualizationDataService.createVisualizationData(
                merchantId,
                VisualizationData.ChartType.pie,
                chartConfig,
                dataMap);
    }

    /**
     * 获取销售概览数据
     *
     * @param merchantId 商家ID
     * @return 销售概览数据
     */
    public Map<String, Object> getSalesOverviewData(Integer merchantId) {
        // 获取商家订单
        List<Order> orders = orderRepository.findByMerchantId(merchantId);

        // 计算总销售额
        double totalSales = 0;
        for (Order order : orders) {
            totalSales += order.getTotalAmount();
        }

        // 计算总订单数
        int totalOrders = orders.size();

        // 计算平均订单价值
        double avgOrderValue = totalOrders > 0 ? totalSales / totalOrders : 0;

        // 按订单状态分组
        Map<String, Long> statusCounts = new HashMap<>();
        for (Order order : orders) {
            String status = order.getOrderStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
        }

        // 最近一个月的销售额
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        double recentSales = 0;
        for (Order order : orders) {
            if (order.getCreatedAt().isAfter(oneMonthAgo)) {
                recentSales += order.getTotalAmount();
            }
        }

        // 创建结果Map
        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("totalOrders", totalOrders);
        result.put("avgOrderValue", avgOrderValue);
        result.put("pendingOrders", statusCounts.getOrDefault("pending", 0L));
        result.put("completedOrders", statusCounts.getOrDefault("completed", 0L));
        result.put("recentSales", recentSales);

        return result;
    }

    /**
     * 获取商家所有可视化数据（包括所有图表）
     *
     * @param merchantId 商家ID
     * @param period 统计周期 (daily, weekly, monthly)
     * @return 所有可视化数据
     */
    public Map<String, Object> getDashboardData(Integer merchantId, String period) {
        // 获取商家可视化数据
        List<VisualizationData> visualizationDataList = visualizationDataService.getVisualizationDataByMerchantId(merchantId);

        // 创建结果Map
        Map<String, Object> dashboardData = new HashMap<>();

        // 销售趋势
        Optional<VisualizationData> salesTrendOpt = visualizationDataList.stream()
                .filter(v -> v.getChartType() == VisualizationData.ChartType.line)
                .findFirst();

        // 如果没有销售趋势数据，则生成一个
        if (salesTrendOpt.isEmpty()) {
            VisualizationData salesTrend = generateSalesTrendVisualization(merchantId, period);
            dashboardData.put("salesTrend", visualizationDataService.parseJson(salesTrend.getDataSnapshot()));
        } else {
            dashboardData.put("salesTrend", visualizationDataService.parseJson(salesTrendOpt.get().getDataSnapshot()));
        }

        // 产品销售分布
        Optional<VisualizationData> productDistributionOpt = visualizationDataList.stream()
                .filter(v -> v.getChartType() == VisualizationData.ChartType.pie)
                .findFirst();

        // 如果没有产品销售分布数据，则生成一个
        if (productDistributionOpt.isEmpty()) {
            VisualizationData productDistribution = generateProductSalesDistribution(merchantId);
            dashboardData.put("productDistribution", visualizationDataService.parseJson(productDistribution.getDataSnapshot()));
        } else {
            dashboardData.put("productDistribution", visualizationDataService.parseJson(productDistributionOpt.get().getDataSnapshot()));
        }

        // 区域销售分布
        Optional<VisualizationData> regionalSalesOpt = visualizationDataList.stream()
                .filter(v -> v.getChartType() == VisualizationData.ChartType.bar)
                .findFirst();

        // 如果没有区域销售分布数据，则生成一个
        if (regionalSalesOpt.isEmpty()) {
            VisualizationData regionalSales = generateRegionalSalesDistribution(merchantId);
            dashboardData.put("regionalSales", visualizationDataService.parseJson(regionalSales.getDataSnapshot()));
        } else {
            dashboardData.put("regionalSales", visualizationDataService.parseJson(regionalSalesOpt.get().getDataSnapshot()));
        }

        // 销售概览
        dashboardData.put("salesOverview", getSalesOverviewData(merchantId));

        return dashboardData;
    }

    /**
     * 按时间周期分组订单
     *
     * @param orders 订单列表
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 按时间周期分组的销售额
     */


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


    /**
     * 按时间周期分组订单
     * @param orders 订单列表
     * @param period 时间周期 (daily, weekly, monthly)
     * @return 按时间周期分组的销售额
     */
    private Map<String, Double> groupOrdersByPeriod(List<Order> orders, String period) {
        Map<String, Double> salesByPeriod = new TreeMap<>();
        DateTimeFormatter formatter;

        // 根据不同周期设置不同的日期格式
        switch (period.toLowerCase()) {
            case "daily":
                formatter = DateTimeFormatter.ofPattern("MM-dd");
                break;
            case "weekly":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'w");
                break;
            case "monthly":
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
        }

        // 根据时间周期筛选订单
        LocalDateTime startDate;
        LocalDateTime now = LocalDateTime.now();

        // 设置不同周期的起始日期
        switch (period.toLowerCase()) {
            case "daily":
                startDate = now.minusDays(30); // 最近30天
                break;
            case "weekly":
                startDate = now.minusWeeks(12); // 最近12周
                break;
            case "monthly":
            default:
                startDate = now.minusMonths(12); // 最近12个月
                break;
        }

        // 仅处理时间范围内的订单
        List<Order> filteredOrders = orders.stream()
                .filter(order -> order.getCreatedAt().isAfter(startDate))
                .collect(Collectors.toList());

        for (Order order : filteredOrders) {
            LocalDateTime orderDate = order.getCreatedAt();
            String periodKey = orderDate.format(formatter);
            salesByPeriod.put(periodKey,
                    salesByPeriod.getOrDefault(periodKey, 0.0) + order.getTotalAmount());
        }

        return salesByPeriod;
    }

}
