package com.ecms.controller;

import com.ecms.entity.Order; // 导入 Order 实体类
import com.ecms.service.OrderService; // 导入 OrderService 接口
import org.springframework.web.bind.annotation.*; // 导入所有 Spring 的 Web 注释
import java.util.List; // 导入 List 集合
import org.springframework.beans.factory.annotation.Autowired; // 导入 @Autowired 注释

@CrossOrigin(origins = "http://localhost:8082") // 允许来自 http://localhost:8081 的跨域请求
@RestController // 声明这个类是一个 REST 控制器
@RequestMapping("/api/orders") // 映射所有请求的基本路径为 /api/orders
public class OrderController {

    @Autowired // 自动注入 OrderService 的实例
    private OrderService orderService;

    // 获取所有订单的 GET 请求
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders(); // 调用服务层获取所有订单
    }

    // 创建新订单的 POST 请求
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order); // 调用服务层创建订单
    }

    // 根据商家ID获取订单的 GET 请求
    @GetMapping("/{merchantId}")
    public List<Order> getOrdersByMerchant(@PathVariable Integer merchantId) {
        return orderService.getOrdersByMerchant(merchantId); // 调用服务层获取特定商家的订单
    }

    // 更新订单的 PUT 请求
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        return orderService.updateOrder(id, order); // 调用服务层更新订单
    }

    // 根据订单ID删除订单的 DELETE 请求
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id); // 调用服务层删除订单
    }

    // 新增模糊查询方法，根据关键字搜索订单
    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam String keyword) {
        return orderService.searchOrders(keyword); // 调用服务层进行模糊查询
    }
}