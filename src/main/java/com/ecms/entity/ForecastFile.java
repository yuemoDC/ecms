package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forecast_files")
@Data
public class ForecastFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer merchantId;

    @Column(nullable = false)
    private String filePath;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
