package com.ecms.repository; // 定义包路径

import com.ecms.entity.Order; // 导入 Order 实体
import org.springframework.data.jpa.repository.JpaRepository; // 导入 JpaRepository 接口
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 导入 JpaSpecificationExecutor 接口

import java.util.List; // 导入 List 接口

// OrderRepository 接口用于操作 Order 实体，并扩展了 JpaRepository 和 JpaSpecificationExecutor
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    // 根据商家 ID 查询所有订单
    List<Order> findByMerchantId(Integer merchantId);
}