package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer merchantId;

    @Column(nullable = false)
    private Integer applicationId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String license;

    private String status = "active";

    @Column(nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private String contactInfo;

    private String businessScope;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
