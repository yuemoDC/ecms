package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "visualization_data")
@Data
public class VisualizationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Integer dataId;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "chart_type", nullable = false)
    private ChartType chartType;

    @Column(name = "data_config", columnDefinition = "json", nullable = false)
    private String dataConfig;

    @Column(name = "data_snapshot", columnDefinition = "json", nullable = false)
    private String dataSnapshot;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ChartType {
        line, bar, pie, heatmap
    }
}
