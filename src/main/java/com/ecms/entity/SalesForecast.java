package com.ecms.entity;

import jakarta.persistence.PrePersist;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售预测实体类
 */
@Entity
@Table(name = "sales_forecasts")
public class SalesForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id")
    private Integer forecastId;

    @Column(name = "merchant_id")
    private Integer merchantId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "forecast_date")
    private LocalDateTime forecastDate;

    @Column(name = "predicted_sales", precision = 12, scale = 2)
    private BigDecimal predictedSales;

    @Column(name = "confidence_interval", columnDefinition = "json")
    private String confidenceInterval;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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

    public LocalDateTime getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDateTime forecastDate) {
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
