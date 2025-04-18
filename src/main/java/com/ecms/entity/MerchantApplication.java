package com.ecms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "merchant_applications")
public class MerchantApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationId;

    @Column(nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private String contactInfo;

    private String businessScope;

    @Column(nullable = false)
    private String license;

    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.pending;

    @Column(nullable = false)
    private Integer userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        pending,
        approved,
        rejected
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (applicationDate == null) {
            applicationDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
