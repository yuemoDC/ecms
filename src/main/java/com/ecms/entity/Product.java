package com.ecms.entity; // 定义包路径

import jakarta.persistence.*; // 导入 JPA 注解
import lombok.Data; // 导入 Lombok 的 @Data 注解
import org.hibernate.annotations.CreationTimestamp; // 导入 CreationTimestamp 注解
import org.hibernate.annotations.UpdateTimestamp; // 导入 UpdateTimestamp 注解

import java.math.BigDecimal; // 导入 BigDecimal 类
import java.time.LocalDateTime; // 导入 LocalDateTime 类
import java.util.ArrayList; // 导入 ArrayList 类
import java.util.List; // 导入 List 接口

@Entity // 表示这个类是一个 JPA 实体，用于映射到数据库表
@Table(name = "products") // 指定这个实体映射到数据库中的 "products" 表
public class Product {
    @Id // 表示这个字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增长策略
    private Integer productId; // 产品的唯一标识符

    @Column(nullable = false) // 此字段不能为空
    private Integer merchantId; // 关联的商家 ID

    @Column(nullable = false) // 此字段不能为空
    private String productName; // 产品名称

    private String description; // 产品描述，可以为 null

    @Column(nullable = false) // 此字段不能为空
    private Double price; // 产品价格

    @Column(nullable = false) // 此字段不能为空
    private Integer stockQuantity; // 产品库存数量

    // 使用字符串表示商品状态
    private String status; // 商品状态，可能为 "active" 或 "inactive"

    private LocalDateTime createdAt; // 产品的创建时间

    private LocalDateTime updatedAt; // 产品的最后更新时间

    // Getters and Setters - 提供对各字段的访问和修改方法
    public Integer getProductId() {
        return productId; // 返回产品ID
    }

    public void setProductId(Integer productId) {
        this.productId = productId; // 设置产品ID
    }

    public Integer getMerchantId() {
        return merchantId; // 返回商家ID
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId; // 设置商家ID
    }

    public String getProductName() {
        return productName; // 返回产品名称
    }

    public void setProductName(String productName) {
        this.productName = productName; // 设置产品名称
    }

    public String getDescription() {
        return description; // 返回产品描述
    }

    public void setDescription(String description) {
        this.description = description; // 设置产品描述
    }

    public Double getPrice() {
        return price; // 返回产品价格
    }

    public void setPrice(Double price) {
        this.price = price; // 设置产品价格
    }

    public Integer getStockQuantity() {
        return stockQuantity; // 返回库存数量
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity; // 设置库存数量
    }

    public String getStatus() {
        return status; // 返回产品状态
    }

    public void setStatus(String status) {
        this.status = status; // 设置产品状态
    }

    public LocalDateTime getCreatedAt() {
        return createdAt; // 返回创建时间
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; // 设置创建时间
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt; // 返回更新时间
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt; // 设置更新时间
    }
}