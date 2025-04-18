package com.ecms.entity; // 定义包路径

import jakarta.persistence.*; // 导入 JPA 注解
import lombok.Data; // 导入 Lombok 的 @Data 注解
import org.hibernate.annotations.CreationTimestamp; // 导入 CreationTimestamp 注解
import org.hibernate.annotations.UpdateTimestamp; // 导入 UpdateTimestamp 注解

import java.time.LocalDateTime; // 导入 LocalDateTime 类

@Entity // 表示这个类是一个 JPA 实体
@Table(name = "orders") // 指明这个实体映射到数据库中的 "orders" 表
@Data // 使用 Lombok 提供的 @Data 注解来自动生成 getters 和 setters
public class Order {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增长策略
    private Integer orderId;

    @Column(nullable = false) // 此字段不能为空
    private Integer merchantId; // 商家ID

    @Column(nullable = false) // 此字段不能为空
    private Integer customerId; // 客户ID

    @Column(nullable = false, unique = true) // 此字段不能为空且唯一
    private String orderNumber; // 订单编号

    @Column(nullable = false) // 此字段不能为空
    private Double totalAmount; // 订单总金额

    // 使用字符串表示订单状态
    @Column(nullable = false) // 此字段不能为空
    private String orderStatus; // 订单状态，如 "pending", "shipped", "completed", "refunded"

    @CreationTimestamp // 自动生成创建时间戳
    @Column(updatable = false) // 在更新时不修改
    private LocalDateTime createdAt; // 记录创建时间

    @UpdateTimestamp // 自动生成更新时间戳
    private LocalDateTime updatedAt; // 记录最后更新时间

    // Getters and Setters (可选，如果使用 @Data 注解则不需要手动定义)
}