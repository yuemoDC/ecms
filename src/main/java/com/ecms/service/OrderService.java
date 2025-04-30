package com.ecms.service;

import com.ecms.entity.Order;
import com.ecms.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    // 获取所有订单
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 创建新订单
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // 根据商家 ID 获取订单
    public List<Order> getOrdersByMerchant(Integer merchantId) {
        return orderRepository.findByMerchantId(merchantId);
    }

    // 更新订单
    public Order updateOrder(Integer id, Order order) {
        order.setOrderId(id);
        return orderRepository.save(order);
    }

    // 删除订单
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    // 根据关键词搜索订单
    public List<Order> searchOrders(String keyword) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNumber"), "%" + keyword + "%")
            );
        });
    }

    /**
     * Get orders for a merchant - for ARIMA prediction
     * @param merchantId merchant ID
     * @return list of orders
     */
    public List<Order> getOrdersByMerchantId(Integer merchantId) {
        logger.info("Retrieving orders for merchant ID={}", merchantId);
        return orderRepository.findByMerchantId(merchantId);
    }

    /**
     * Get orders for a merchant and product - for ARIMA prediction
     * @param merchantId merchant ID
     * @param productId product ID
     * @return list of orders
     */
    public List<Order> getOrdersByMerchantIdAndProductId(Integer merchantId, Integer productId) {
        logger.info("Retrieving orders for merchant ID={}, product ID={}", merchantId, productId);
        return orderRepository.findByMerchantIdAndProductId(merchantId, productId);
    }

    /**
     * Get orders for a merchant within date range
     * @param merchantId merchant ID
     * @param startDate start date
     * @param endDate end date
     * @return list of orders
     */
    public List<Order> getOrdersByMerchantIdAndDateRange(Integer merchantId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving orders for merchant ID={} between {} and {}", merchantId, startDate, endDate);
        return orderRepository.findByMerchantIdAndCreatedAtBetween(merchantId, startDate, endDate);
    }
}
