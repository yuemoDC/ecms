package com.ecms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_forecasts")
public class SalesPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id")
    private Integer forecastId;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "predicted_sales", nullable = false, precision = 12, scale = 2)
    private BigDecimal predictedSales;

    @Column(name = "confidence_interval", columnDefinition = "json")
    private String confidenceInterval;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 默认构造函数
    public SalesPrediction() {
    }

    // Getters and Setters
    public Integer getForecastId() {
        return forecastId;
    }

    public void setForecastId(Integer forecastId) {
        this.forecastId = forecastId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDate forecastDate) {
        this.forecastDate = forecastDate;
    }

    public BigDecimal getPredictedSales() {
        return predictedSales;
    }

    public void setPredictedSales(BigDecimal predictedSales) {
        this.predictedSales = predictedSales;
    }

    public String getConfidenceInterval() {
        return confidenceInterval;
    }

    public void setConfidenceInterval(String confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 提供预测日期转字符串方法，用于保存到数据库
    public String getPredictionDate() {
        return forecastDate != null ? forecastDate.toString() : null;
    }

    // 从字符串设置预测日期
    public void setPredictionDate(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            this.forecastDate = LocalDate.parse(dateStr);
        }
    }
}
