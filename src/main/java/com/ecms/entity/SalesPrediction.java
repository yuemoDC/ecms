package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sales_forecasts") // 使用现有表名
public class SalesPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id")  // 映射到forecast_id列
    private Integer predictionId;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    // 产品ID是必需的
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "forecast_date", nullable = false)
    private LocalDateTime forecastDate;  // 使用LocalDateTime而非LocalDate

    @Column(name = "predicted_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal predictedSales;

    // 预测置信区间，使用明确的列名映射，避免冲突
    @Column(name = "confidence_interval", columnDefinition = "JSON")
    private String confidenceInterval;

    // 创建时间
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
