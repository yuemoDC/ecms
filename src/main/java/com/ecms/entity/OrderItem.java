package com.ecms.entity; // 定义包路径

import jakarta.persistence.*; // 导入 JPA 注解
import lombok.Data; // 导入 Lombok 的 @Data 注解

@Entity // 表示这个类是一个 JPA 实体
@Table(name = "order_items") // 指明这个实体映射到数据库中的 "order_items" 表
@Data // 使用 Lombok 提供的 @Data 注解来自动生成 getters 和 setters
public class OrderItem {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增长策略
    private Integer itemId; // 订单项ID

    @Column(nullable = false) // 此字段不能为空
    private Integer orderId; // 关联的订单ID

    @Column(nullable = false) // 此字段不能为空
    private Integer productId; // 关联的产品ID

    @Column(nullable = false) // 此字段不能为空
    private Integer quantity; // 订单项的数量

    @Column(nullable = false) // 此字段不能为空
    private Double unitPrice; // 产品的单价

    // Getters and Setters (可选，如果使用 @Data 注解则不需要手动定义)
}