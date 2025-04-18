package com.ecms.service; // 定义包路径

import com.ecms.entity.Order; // 导入 Order 实体
import com.ecms.repository.OrderRepository; // 导入 OrderRepository 接口
import org.springframework.beans.factory.annotation.Autowired; // 导入 Autowired 注解
import org.springframework.stereotype.Service; // 导入 Service 注解

import java.util.List; // 导入 List 接口

@Service // 标识这个类为服务层的组件
public class OrderService {

    @Autowired // 自动注入 OrderRepository
    private OrderRepository orderRepository;

    // 获取所有订单
    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // 调用 repository 方法获取所有订单
    }

    // 创建新订单
    public Order createOrder(Order order) {
        return orderRepository.save(order); // 保存新订单并返回
    }

    // 根据商家 ID 获取订单
    public List<Order> getOrdersByMerchant(Integer merchantId) {
        return orderRepository.findByMerchantId(merchantId); // 调用自定义方法获取指定商家的订单
    }

    // 更新订单
    public Order updateOrder(Integer id, Order order) {
        order.setOrderId(id); // 设置订单 ID，以确保更新正确的订单
        return orderRepository.save(order); // 保存更新后的订单
    }

    // 删除订单
    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id); // 调用 repository 方法删除指定 ID 的订单
    }

    // 根据关键词搜索订单
    public List<Order> searchOrders(String keyword) {
        return orderRepository.findAll((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction(); // 如果没有关键词，返回所有记录
            }
            // 使用关键字进行模糊查询
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNumber"), "%" + keyword + "%") // 按订单号模糊查询
            );
        });
    }
}